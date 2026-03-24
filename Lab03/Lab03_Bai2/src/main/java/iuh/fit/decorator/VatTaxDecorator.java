package iuh.fit.decorator;


import iuh.fit.strategy.TaxStrategy;
import iuh.fit.strategy.VatTaxStrategy;

public class VatTaxDecorator extends TaxDecorator {
    private TaxStrategy taxStrategy;

    public VatTaxDecorator(TaxableItem item) {
        super(item);
        this.taxStrategy = new VatTaxStrategy();
    }

    @Override
    public double getPrice() {
        double base = item.getPrice();
        return base + taxStrategy.calculateTax(base);
    }

    @Override
    public String getDescription() {
        double base = item.getPrice();
        double tax = taxStrategy.calculateTax(base);
        return item.getDescription() + " + " + taxStrategy.getTaxName() + ": " + tax;
    }
}