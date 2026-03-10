package fit.iuh;


import fit.iuh.factor_class.NotificationFactory;
import fit.iuh.interfaces.Notification;

// Kiểm tra
public class Main {
    public static void main(String[] args) {
        NotificationFactory factory = new NotificationFactory();

        Notification n1 = factory.createNotification("EMAIL");
        n1.notifyUser();

        Notification n2 = factory.createNotification("SMS");
        n2.notifyUser();
    }
}