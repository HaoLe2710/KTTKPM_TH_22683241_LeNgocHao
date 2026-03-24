package iuh.fit.strategy;

public class LuxuryTaxStrategy implements TaxStrategy {
    private static final double LUXURY_RATE = 0.15;

    @Override
    public double calculateTax(double basePrice) {
        return basePrice * LUXURY_RATE;
    }

    @Override
    public String getTaxName() {
        return "Thuế xa xỉ (15%)";
    }
}