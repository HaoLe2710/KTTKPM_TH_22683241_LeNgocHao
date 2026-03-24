package iuh.fit.decorator;

public abstract class PaymentDecorator implements PaymentComponent {
    protected PaymentComponent component;

    public PaymentDecorator(PaymentComponent component) {
        this.component = component;
    }

    @Override
    public double getFinalAmount() {
        return component.getFinalAmount();
    }

    @Override
    public String getDescription() {
        return component.getDescription();
    }
}