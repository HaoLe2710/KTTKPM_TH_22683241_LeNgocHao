package iuh.fit.state;


import iuh.fit.model.PaymentTransaction;

public class ProcessingState implements PaymentState {

    @Override
    public void handle(PaymentTransaction transaction) {
        System.out.println("Giao dịch [" + transaction.getTransactionId() + "] ở trạng thái ĐANG XỬ LÝ.");
        System.out.println("Đang gửi yêu cầu thanh toán tới cổng thanh toán...");
        transaction.setState(new SuccessfulState());
    }

    @Override
    public String getStateName() {
        return "Đang xử lý";
    }
}