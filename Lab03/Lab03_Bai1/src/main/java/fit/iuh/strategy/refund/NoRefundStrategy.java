package fit.iuh.strategy.refund;


import fit.iuh.model.Order;

public class NoRefundStrategy implements RefundStrategy {
    @Override
    public void refund(Order order) {
        System.out.println("Đơn hàng này không áp dụng hoàn tiền.");
    }
}