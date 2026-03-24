package iuh.fit.decorator;


import iuh.fit.strategy.LuxuryTaxStrategy;
import iuh.fit.strategy.TaxStrategy;

public class LuxuryTaxDecorator extends TaxDecorator {
    private TaxStrategy taxStrategy;

    public LuxuryTaxDecorator(TaxableItem item) {
        super(item);
        this.taxStrategy = new LuxuryTaxStrategy();
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