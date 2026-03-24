package     fit.iuh;


import fit.iuh.decorator.BasicOrderService;
import fit.iuh.decorator.LoggingOrderServiceDecorator;
import fit.iuh.decorator.NotificationOrderServiceDecorator;
import fit.iuh.decorator.OrderService;
import fit.iuh.model.Order;
import fit.iuh.state.CancelledOrderState;
import fit.iuh.strategy.refund.BankRefundStrategy;
import fit.iuh.strategy.refund.WalletRefundStrategy;
import fit.iuh.strategy.shipping.ExpressShippingStrategy;
import fit.iuh.strategy.shipping.StandardShippingStrategy;

public class Main {
    public static void main(String[] args) {
        System.out.println("===== KỊCH BẢN 1: ĐƠN HÀNG GIAO THÀNH CÔNG =====");
        Order order1 = new Order(
                "ORD001",
                "Nguyen Van A",
                500000,
                new ExpressShippingStrategy(),
                new BankRefundStrategy()
        );

        OrderService service1 = new NotificationOrderServiceDecorator(
                new LoggingOrderServiceDecorator(
                        new BasicOrderService()
                )
        );

        service1.execute(order1);
        System.out.println();

        service1.execute(order1);
        System.out.println();

        service1.execute(order1);
        System.out.println();

        System.out.println("===== KỊCH BẢN 2: ĐƠN HÀNG BỊ HỦY =====");
        Order order2 = new Order(
                "ORD002",
                "Tran Thi B",
                300000,
                new StandardShippingStrategy(),
                new WalletRefundStrategy()
        );

        order2.setState(new CancelledOrderState());

        OrderService service2 = new NotificationOrderServiceDecorator(
                new LoggingOrderServiceDecorator(
                        new BasicOrderService()
                )
        );

        service2.execute(order2);
    }
}