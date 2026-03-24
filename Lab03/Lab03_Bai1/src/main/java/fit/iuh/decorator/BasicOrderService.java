package fit.iuh.decorator;


import fit.iuh.model.Order;

public class BasicOrderService implements OrderService {
    @Override
    public void execute(Order order) {
        order.process();
    }
}