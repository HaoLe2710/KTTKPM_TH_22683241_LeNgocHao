package iuh.fit.decorator;

public class BasePaymentComponent implements PaymentComponent {
    private double amount;

    public BasePaymentComponent(double amount) {
        this.amount = amount;
    }

    @Override
    public double getFinalAmount() {
        return amount;
    }

    @Override
    public String getDescription() {
        return "Số tiền gốc: " + amount;
    }
}