package iuh.fit;


import iuh.fit.decorator.BasePaymentComponent;
import iuh.fit.decorator.DiscountDecorator;
import iuh.fit.decorator.PaymentComponent;
import iuh.fit.decorator.ProcessingFeeDecorator;
import iuh.fit.model.PaymentTransaction;
import iuh.fit.state.FailedState;
import iuh.fit.strategy.CreditCardPaymentStrategy;
import iuh.fit.strategy.PayPalPaymentStrategy;

public class Main {
    public static void main(String[] args) {
        System.out.println("===== KỊCH BẢN 1: THANH TOÁN BẰNG THẺ TÍN DỤNG + PHÍ XỬ LÝ =====");
        PaymentTransaction transaction1 = new PaymentTransaction(
                "TXN001",
                1000000,
                new CreditCardPaymentStrategy()
        );

        PaymentComponent payment1 = new BasePaymentComponent(transaction1.getOriginalAmount());
        payment1 = new ProcessingFeeDecorator(payment1, 0.02);

        System.out.println(payment1.getDescription());
        System.out.println("Số tiền cuối cùng: " + payment1.getFinalAmount());

        transaction1.processState();
        transaction1.processState();
        transaction1.getPaymentStrategy().pay(payment1.getFinalAmount());
        transaction1.processState();

        System.out.println();

        System.out.println("===== KỊCH BẢN 2: THANH TOÁN BẰNG PAYPAL + PHÍ XỬ LÝ + MÃ GIẢM GIÁ =====");
        PaymentTransaction transaction2 = new PaymentTransaction(
                "TXN002",
                2000000,
                new PayPalPaymentStrategy()
        );

        PaymentComponent payment2 = new BasePaymentComponent(transaction2.getOriginalAmount());
        payment2 = new ProcessingFeeDecorator(payment2, 0.03);
        payment2 = new DiscountDecorator(payment2, 150000);

        System.out.println(payment2.getDescription());
        System.out.println("Số tiền cuối cùng: " + payment2.getFinalAmount());

        transaction2.processState();
        transaction2.processState();
        transaction2.getPaymentStrategy().pay(payment2.getFinalAmount());
        transaction2.processState();

        System.out.println();

        System.out.println("===== KỊCH BẢN 3: THANH TOÁN THẤT BẠI =====");
        PaymentTransaction transaction3 = new PaymentTransaction(
                "TXN003",
                500000,
                new CreditCardPaymentStrategy()
        );

        PaymentComponent payment3 = new BasePaymentComponent(transaction3.getOriginalAmount());
        payment3 = new DiscountDecorator(payment3, 50000);

        System.out.println(payment3.getDescription());
        System.out.println("Số tiền cuối cùng: " + payment3.getFinalAmount());

        transaction3.processState();
        transaction3.setState(new FailedState());
        transaction3.processState();
    }
}