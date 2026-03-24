package iuh.fit.decorator;

public class DiscountDecorator extends PaymentDecorator {
    private double discountAmount;

    public DiscountDecorator(PaymentComponent component, double discountAmount) {
        super(component);
        this.discountAmount = discountAmount;
    }

    @Override
    public double getFinalAmount() {
        double current = component.getFinalAmount();
        double result = current - discountAmount;
        return Math.max(result, 0);
    }

    @Override
    public String getDescription() {
        return component.getDescription() + " - Giảm giá: " + discountAmount;
    }
}