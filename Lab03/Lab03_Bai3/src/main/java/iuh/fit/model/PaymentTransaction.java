package iuh.fit.model;


import iuh.fit.state.InitiatedState;
import iuh.fit.state.PaymentState;
import iuh.fit.strategy.PaymentStrategy;

public class PaymentTransaction {
    private String transactionId;
    private double originalAmount;
    private PaymentState state;
    private PaymentStrategy paymentStrategy;

    public PaymentTransaction(String transactionId, double originalAmount, PaymentStrategy paymentStrategy) {
        this.transactionId = transactionId;
        this.originalAmount = originalAmount;
        this.paymentStrategy = paymentStrategy;
        this.state = new InitiatedState();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public double getOriginalAmount() {
        return originalAmount;
    }

    public PaymentState getState() {
        return state;
    }

    public void setState(PaymentState state) {
        this.state = state;
    }

    public PaymentStrategy getPaymentStrategy() {
        return paymentStrategy;
    }

    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public void processState() {
        state.handle(this);
    }
}