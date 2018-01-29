package cm.aptoide.pt.billing;

public interface BillingServiceFactory {

  public BillingService create(String merchantPackageName);

}
