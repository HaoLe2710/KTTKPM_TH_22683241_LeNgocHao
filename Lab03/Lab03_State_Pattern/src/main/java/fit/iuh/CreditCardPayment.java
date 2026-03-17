package fit.iuh;

class CreditCardPayment implements PaymentStrategy {
    public void pay(double amount) { System.out.println("Thanh toán " + amount + " qua Thẻ tín dụng."); }
}