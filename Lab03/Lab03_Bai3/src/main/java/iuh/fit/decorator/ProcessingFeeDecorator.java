package iuh.fit.decorator;

public class ProcessingFeeDecorator extends PaymentDecorator {
    private double feeRate;

    public ProcessingFeeDecorator(PaymentComponent component, double feeRate) {
        super(component);
        this.feeRate = feeRate;
    }

    @Override
    public double getFinalAmount() {
        double current = component.getFinalAmount();
        return current + (current * feeRate);
    }

    @Override
    public String getDescription() {
        return component.getDescription() + " + Phí xử lý (" + (feeRate * 100) + "%)";
    }
}