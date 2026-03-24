package iuh.fit.state;


import iuh.fit.model.PaymentTransaction;

public class SuccessfulState implements PaymentState {

    @Override
    public void handle(PaymentTransaction transaction) {
        System.out.println("Giao dịch [" + transaction.getTransactionId() + "] THANH TOÁN THÀNH CÔNG.");
    }

    @Override
    public String getStateName() {
        return "Thành công";
    }
}