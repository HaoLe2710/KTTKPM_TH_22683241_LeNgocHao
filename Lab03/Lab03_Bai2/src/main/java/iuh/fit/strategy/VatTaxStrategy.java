package iuh.fit.strategy;

public class VatTaxStrategy implements TaxStrategy {
    private static final double VAT_RATE = 0.10;

    @Override
    public double calculateTax(double basePrice) {
        return basePrice * VAT_RATE;
    }

    @Override
    public String getTaxName() {
        return "VAT (10%)";
    }
}