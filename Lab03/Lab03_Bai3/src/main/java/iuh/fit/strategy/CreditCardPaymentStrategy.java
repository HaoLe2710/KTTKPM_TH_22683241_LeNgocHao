package iuh.fit.strategy;

public class CreditCardPaymentStrategy implements PaymentStrategy {

    @Override
    public void pay(double amount) {
        System.out.println("Thanh toán bằng THẺ TÍN DỤNG với số tiền: " + amount);
    }

    @Override
    public String getMethodName() {
        return "Thẻ tín dụng";
    }
}