package iuh.fit.decorator;

public abstract class TaxDecorator implements TaxableItem {
    protected TaxableItem item;

    public TaxDecorator(TaxableItem item) {
        this.item = item;
    }

    @Override
    public double getPrice() {
        return item.getPrice();
    }

    @Override
    public String getDescription() {
        return item.getDescription();
    }
}