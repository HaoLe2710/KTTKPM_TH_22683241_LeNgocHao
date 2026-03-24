package iuh.fit.state;


import iuh.fit.model.PaymentTransaction;

public interface PaymentState {
    void handle(PaymentTransaction transaction);
    String getStateName();
}