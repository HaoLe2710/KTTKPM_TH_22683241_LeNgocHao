package iuh.fit.decorator;


import iuh.fit.strategy.ConsumptionTaxStrategy;
import iuh.fit.strategy.TaxStrategy;

public class ConsumptionTaxDecorator extends TaxDecorator {
    private TaxStrategy taxStrategy;

    public ConsumptionTaxDecorator(TaxableItem item) {
        super(item);
        this.taxStrategy = new ConsumptionTaxStrategy();
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