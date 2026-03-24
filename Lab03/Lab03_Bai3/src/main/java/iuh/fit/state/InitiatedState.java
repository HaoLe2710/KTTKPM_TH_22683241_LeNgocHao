package iuh.fit.state;


import iuh.fit.model.PaymentTransaction;

public class InitiatedState implements PaymentState {

    @Override
    public void handle(PaymentTransaction transaction) {
        System.out.println("Giao dịch [" + transaction.getTransactionId() + "] ở trạng thái KHỞI TẠO.");
        System.out.println("Kiểm tra thông tin giao dịch...");
        transaction.setState(new ProcessingState());
    }

    @Override
    public String getStateName() {
        return "Khởi tạo";
    }
}