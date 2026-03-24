package iuh.fit.strategy;

public class PayPalPaymentStrategy implements PaymentStrategy {

    @Override
    public void pay(double amount) {
        System.out.println("Thanh toán bằng PAYPAL với số tiền: " + amount);
    }

    @Override
    public String getMethodName() {
        return "PayPal";
    }
}