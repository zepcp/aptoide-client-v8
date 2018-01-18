package cm.aptoide.pt.billing.payment;

public class CreditCard {

  private final String cardNumber;
  private final String expirationMonth;
  private final String expirationYear;
  private final String cvv;

  public CreditCard(String cardNumber, String expirationMonth, String expirationYear,
      String cvv) {
    this.cardNumber = cardNumber;
    this.expirationMonth = expirationMonth;
    this.expirationYear = expirationYear;
    this.cvv = cvv;
  }

  public String getCardNumber() {
    return cardNumber;
  }

  public String getExpirationMonth() {
    return expirationMonth;
  }

  public String getExpirationYear() {
    return expirationYear;
  }

  public String getCvv() {
    return cvv;
  }
}
