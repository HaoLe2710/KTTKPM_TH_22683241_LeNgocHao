package fit.iuh.strategy.refund;


import fit.iuh.model.Order;

public class BankRefundStrategy implements RefundStrategy {
    @Override
    public void refund(Order order) {
        System.out.println("Hoàn tiền qua TÀI KHOẢN NGÂN HÀNG số tiền: " + order.getAmount());
    }
}