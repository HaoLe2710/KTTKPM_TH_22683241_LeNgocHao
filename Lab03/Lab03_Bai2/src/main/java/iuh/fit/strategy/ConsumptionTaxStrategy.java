package iuh.fit.strategy;

public class ConsumptionTaxStrategy implements TaxStrategy {
    private static final double CONSUMPTION_RATE = 0.08;

    @Override
    public double calculateTax(double basePrice) {
        return basePrice * CONSUMPTION_RATE;
    }

    @Override
    public String getTaxName() {
        return "Thuế tiêu thụ (8%)";
    }
}