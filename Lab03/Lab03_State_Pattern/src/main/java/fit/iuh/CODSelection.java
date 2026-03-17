package fit.iuh;

class CODSelection implements PaymentStrategy {
    public void pay(double amount) { System.out.println("Sẽ thanh toán " + amount + " khi nhận hàng (COD)."); }
}