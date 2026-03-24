package iuh.fit.state;


import iuh.fit.model.PaymentTransaction;

public class FailedState implements PaymentState {

    @Override
    public void handle(PaymentTransaction transaction) {
        System.out.println("Giao dịch [" + transaction.getTransactionId() + "] THANH TOÁN THẤT BẠI.");
    }

    @Override
    public String getStateName() {
        return "Thất bại";
    }
}