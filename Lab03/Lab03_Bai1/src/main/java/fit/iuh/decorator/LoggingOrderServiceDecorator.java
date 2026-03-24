package fit.iuh.decorator;


import fit.iuh.model.Order;

public class LoggingOrderServiceDecorator extends OrderServiceDecorator {

    public LoggingOrderServiceDecorator(OrderService wrappedService) {
        super(wrappedService);
    }

    @Override
    public void execute(Order order) {
        System.out.println("[LOG] Bắt đầu xử lý đơn hàng: " + order.getOrderId()
                + " | Trạng thái hiện tại: " + order.getState().getStateName());
        super.execute(order);
        System.out.println("[LOG] Kết thúc xử lý đơn hàng: " + order.getOrderId()
                + " | Trạng thái mới: " + order.getState().getStateName());
    }
}