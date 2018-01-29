package cm.aptoide.pt.billing.payment;

import adyen.com.adyencse.encrypter.ClientSideEncrypter;
import adyen.com.adyencse.encrypter.exception.EncrypterException;
import android.content.Context;
import android.support.annotation.NonNull;
import com.adyen.core.PaymentRequest;
import com.adyen.core.interfaces.PaymentDataCallback;
import com.adyen.core.interfaces.PaymentDetailsCallback;
import com.adyen.core.interfaces.PaymentMethodCallback;
import com.adyen.core.interfaces.PaymentRequestDetailsListener;
import com.adyen.core.interfaces.PaymentRequestListener;
import com.adyen.core.interfaces.UriCallback;
import com.adyen.core.models.PaymentMethod;
import com.adyen.core.models.PaymentRequestResult;
import com.adyen.core.models.paymentdetails.CreditCardPaymentDetails;
import com.adyen.core.models.paymentdetails.InputDetail;
import com.adyen.core.models.paymentdetails.PaymentDetails;
import com.jakewharton.rxrelay.PublishRelay;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.Single;

public class Adyen {

  private final Context context;
  private final Charset dataCharset;
  private final Scheduler scheduler;

  private PublishRelay<AdyenPaymentStatus> status;
  private PaymentRequest paymentRequest;
  private DetailsStatus detailsStatus;
  private PaymentStatus paymentStatus;

  public Adyen(Context context, Charset dataCharset, Scheduler scheduler,
      PublishRelay<AdyenPaymentStatus> paymentRequestStatus) {
    this.context = context;
    this.dataCharset = dataCharset;
    this.scheduler = scheduler;
    this.status = paymentRequestStatus;
  }

  public Single<String> createSession() {

    paymentStatus = new PaymentStatus(status);
    detailsStatus = new DetailsStatus(status, Collections.emptyList());
    paymentRequest = new PaymentRequest(context, paymentStatus, detailsStatus);
    paymentRequest.start();

    return getStatus().filter(status -> status.getToken() != null)
        .map(status -> status.getToken())
        .first()
        .toSingle();
  }

  public Completable openSession(String session) {
    return getStatus().takeUntil(status -> status.getDetailsCallback() != null)
        .flatMapCompletable(status -> {

          if (status.getDetailsCallback() != null) {
            // Ready to listen to credit card details.
            return Completable.complete();
          }

          if (status.getServices() != null && status.getServiceCallback() != null) {
            // Ready to select payment method. We always use credit card for now.
            return getAdyenPaymentMethod(status.getServices(), PaymentMethod.Type.CARD).doOnNext(
                method -> status.getServiceCallback()
                    .completionWithPaymentMethod(method))
                .toCompletable();
          }

          if (status.getDataCallback() != null) {
            // Ready to open session.
            status.getDataCallback()
                .completionWithPaymentData(session.getBytes(dataCharset));
          }
          return Completable.complete();
        })
        .toCompletable();
  }

  public void closeSession() {

    if (paymentRequest != null) {
      detailsStatus.clearStatus();
      paymentStatus.clearStatus();
      paymentRequest.cancel();
    }
  }

  public Single<String> registerCreditCard(CreditCard creditCard) {
    return getStatus().flatMap(status -> {

      if (status.getResult() != null) {
        if (status.getResult()
            .getError() != null) {
          return Observable.error(status.getResult()
              .getError());
        }

        if (status.getResult()
            .isProcessed()) {
          return Observable.just(status.getResult()
              .getPayment()
              .getPayload());
        }
      }

      if (status.getRedirectUrl() != null || status.getUriCallback() != null) {
        return Observable.error(
            new IllegalStateException("Not possible to register credit card 3D secure required."));
      }

      if (status.getDetailsCallback() == null) {
        return Observable.error(new IllegalStateException(
            "Not possible to register credit card callbacks unavailable."));
      }

      return getAdyenPaymentMethod(status.getServices(), PaymentMethod.Type.CARD).flatMapSingle(
          paymentMethod -> Single.fromCallable(
              () -> fromCreditCard(creditCard, paymentMethod.getInputDetails(),
                  status.getPaymentRequest()
                      .getPublicKey(), status.getPaymentRequest()
                      .getGenerationTime())))
          .doOnNext(details -> status.getDetailsCallback()
              .completionWithPaymentDetails(details))
          .ignoreElements()
          .cast(String.class);
    })
        .first()
        .toSingle();
  }

  private PaymentDetails fromCreditCard(CreditCard creditCard, Collection<InputDetail> inputDetails,
      String publicKey, String generationTime) throws JSONException, EncrypterException {
    final CreditCardPaymentDetails creditCardPaymentDetails =
        new CreditCardPaymentDetails(inputDetails);
    final JSONObject sensitiveData = new JSONObject();

    sensitiveData.put("holderName", "Checkout Shopper Placeholder");
    sensitiveData.put("number", creditCard.getCardNumber());
    sensitiveData.put("expiryMonth", creditCard.getExpirationMonth());
    sensitiveData.put("expiryYear", creditCard.getExpirationYear());
    sensitiveData.put("generationtime", generationTime);
    sensitiveData.put("cvc", creditCard.getCvv());
    creditCardPaymentDetails.fillCardToken(
        new ClientSideEncrypter(publicKey).encrypt(sensitiveData.toString()));
    creditCardPaymentDetails.fillStoreDetails(true);
    return creditCardPaymentDetails;
  }

  private Observable<PaymentMethod> getAdyenPaymentMethod(List<PaymentMethod> services,
      String paymentType) {
    return Observable.from(services)
        .filter(service -> paymentType.equals(service.getType()))
        .take(1);
  }

  private Observable<AdyenPaymentStatus> getStatus() {
    return status.startWith((AdyenPaymentStatus) null)
        .map(event -> new AdyenPaymentStatus(paymentStatus.getToken(),
            paymentStatus.getDataCallback(), paymentStatus.getResult(),
            detailsStatus.getServiceCallback(), detailsStatus.getServices(),
            detailsStatus.getDetailsCallback(), detailsStatus.getPaymentRequest(),
            detailsStatus.getRedirectUrl(), detailsStatus.getUriCallback()))
        .subscribeOn(scheduler);
  }

  public static class PaymentStatus implements PaymentRequestListener {

    private PublishRelay<AdyenPaymentStatus> status;
    private String token;
    private PaymentDataCallback dataCallback;
    private PaymentRequestResult result;

    public PaymentStatus(PublishRelay<AdyenPaymentStatus> status) {
      this.status = status;
    }

    @Override public void onPaymentDataRequested(@NonNull PaymentRequest paymentRequest,
        @NonNull String token, @NonNull PaymentDataCallback paymentDataCallback) {
      this.token = token;
      this.dataCallback = paymentDataCallback;
      notifyStatus();
    }

    @Override public void onPaymentResult(@NonNull PaymentRequest paymentRequest,
        @NonNull PaymentRequestResult paymentRequestResult) {
      this.result = paymentRequestResult;
      notifyStatus();
    }

    public String getToken() {
      return token;
    }

    public PaymentDataCallback getDataCallback() {
      return dataCallback;
    }

    public PaymentRequestResult getResult() {
      return result;
    }

    public void clearStatus() {
      this.status = null;
    }

    private void notifyStatus() {
      if (status != null) {
        this.status.call(null);
      }
    }
  }

  private static class DetailsStatus implements PaymentRequestDetailsListener {

    private PublishRelay<AdyenPaymentStatus> status;
    private PaymentMethodCallback serviceCallback;
    private List<PaymentMethod> services;
    private PaymentDetailsCallback detailsCallback;
    private PaymentRequest paymentRequest;
    private UriCallback uriCallback;
    private String redirectUrl;

    public DetailsStatus(PublishRelay<AdyenPaymentStatus> status, List<PaymentMethod> services) {
      this.status = status;
      this.services = services;
    }

    @Override public void onPaymentMethodSelectionRequired(@NonNull PaymentRequest paymentRequest,
        @NonNull List<PaymentMethod> recurringServices, @NonNull List<PaymentMethod> otherServices,
        @NonNull PaymentMethodCallback paymentMethodCallback) {
      this.serviceCallback = paymentMethodCallback;
      this.services = otherServices != null ? otherServices : Collections.emptyList();
      notifyStatus();
    }

    @Override public void onRedirectRequired(@NonNull PaymentRequest paymentRequest,
        @NonNull String redirectUrl, @NonNull UriCallback uriCallback) {
      this.uriCallback = uriCallback;
      this.redirectUrl = redirectUrl;
      notifyStatus();
    }

    @Override public void onPaymentDetailsRequired(@NonNull PaymentRequest paymentRequest,
        @NonNull Collection<InputDetail> inputDetails,
        @NonNull PaymentDetailsCallback paymentDetailsCallback) {
      this.detailsCallback = paymentDetailsCallback;
      this.paymentRequest = paymentRequest;
      notifyStatus();
    }

    public PaymentMethodCallback getServiceCallback() {
      return serviceCallback;
    }

    public List<PaymentMethod> getServices() {
      return services;
    }

    public PaymentDetailsCallback getDetailsCallback() {
      return detailsCallback;
    }

    public PaymentRequest getPaymentRequest() {
      return paymentRequest;
    }

    public UriCallback getUriCallback() {
      return uriCallback;
    }

    public String getRedirectUrl() {
      return redirectUrl;
    }

    public void clearStatus() {
      this.status = null;
    }

    private void notifyStatus() {
      if (status != null) {
        this.status.call(null);
      }
    }
  }
}
