package fit.iuh.state;


import fit.iuh.model.Order;

public class NewOrderState implements OrderState {

    @Override
    public void handle(Order order) {
        System.out.println("== Đơn hàng ở trạng thái: MỚI TẠO ==");
        System.out.println("Kiểm tra thông tin đơn hàng: " + order.getOrderId());
        System.out.println("Khách hàng: " + order.getCustomerName());
        System.out.println("Tổng tiền: " + order.getAmount());

        System.out.println("Thông tin hợp lệ -> chuyển sang trạng thái ĐANG XỬ LÝ");
        order.setState(new ProcessingOrderState());
    }

    @Override
    public String getStateName() {
        return "Mới tạo";
    }
}