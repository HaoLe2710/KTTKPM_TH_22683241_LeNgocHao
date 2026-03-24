package fit.iuh.state;


import fit.iuh.model.Order;

public interface OrderState {
    void handle(Order order);
    String getStateName();
}