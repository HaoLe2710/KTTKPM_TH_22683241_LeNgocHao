package fit.iuh.strategy.refund;


import fit.iuh.model.Order;

public class WalletRefundStrategy implements RefundStrategy {
    @Override
    public void refund(Order order) {
        System.out.println("Hoàn tiền qua VÍ ĐIỆN TỬ số tiền: " + order.getAmount());
    }
}