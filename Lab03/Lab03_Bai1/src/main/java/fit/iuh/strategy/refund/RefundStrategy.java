package fit.iuh.strategy.refund;


import fit.iuh.model.Order;

public interface RefundStrategy {
    void refund(Order order);
}