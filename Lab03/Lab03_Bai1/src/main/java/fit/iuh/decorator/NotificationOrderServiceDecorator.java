package fit.iuh.decorator;


import fit.iuh.model.Order;

public class NotificationOrderServiceDecorator extends OrderServiceDecorator {

    public NotificationOrderServiceDecorator(OrderService wrappedService) {
        super(wrappedService);
    }

    @Override
    public void execute(Order order) {
        super.execute(order);
        System.out.println("[NOTIFICATION] Đã gửi thông báo cho khách hàng: " + order.getCustomerName());
    }
}