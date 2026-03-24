package fit.iuh.strategy.shipping;


import fit.iuh.model.Order;

public interface ShippingStrategy {
    void ship(Order order);
}