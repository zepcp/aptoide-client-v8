package cm.aptoide.pt.billing.payment;

public class CreditCard {

  private String cardNumber;
  private String expirationMonth;
  private String expirationYear;
  private String cvv;

  public CreditCard(String cardNumber, String expirationMonth, String expirationYear, String cvv) {
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
