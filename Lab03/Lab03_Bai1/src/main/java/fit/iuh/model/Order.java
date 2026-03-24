package fit.iuh.model;


import fit.iuh.state.NewOrderState;
import fit.iuh.state.OrderState;
import fit.iuh.strategy.refund.RefundStrategy;
import fit.iuh.strategy.shipping.ShippingStrategy;

public class Order {
    private String orderId;
    private String customerName;
    private double amount;
    private OrderState state;
    private ShippingStrategy shippingStrategy;
    private RefundStrategy refundStrategy;

    public Order(String orderId, String customerName, double amount,
                 ShippingStrategy shippingStrategy,
                 RefundStrategy refundStrategy) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.amount = amount;
        this.shippingStrategy = shippingStrategy;
        this.refundStrategy = refundStrategy;
        this.state = new NewOrderState();
    }

    public void process() {
        state.handle(this);
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getAmount() {
        return amount;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public ShippingStrategy getShippingStrategy() {
        return shippingStrategy;
    }

    public RefundStrategy getRefundStrategy() {
        return refundStrategy;
    }
}