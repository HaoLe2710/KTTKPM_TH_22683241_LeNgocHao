package fit.iuh.decorator;


import fit.iuh.model.Order;

public abstract class OrderServiceDecorator implements OrderService {
    protected OrderService wrappedService;

    public OrderServiceDecorator(OrderService wrappedService) {
        this.wrappedService = wrappedService;
    }

    @Override
    public void execute(Order order) {
        wrappedService.execute(order);
    }
}