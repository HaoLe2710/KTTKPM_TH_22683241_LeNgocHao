package fit.iuh.factor_class;

import fit.iuh.concretes.EmailNotification;
import fit.iuh.concretes.SMSNotification;
import fit.iuh.interfaces.Notification;

public class NotificationFactory {
    public Notification createNotification(String type) {
        if (type == null || type.isEmpty()) return null;

        return switch (type.toLowerCase()) {
            case "email" -> new EmailNotification();
            case "sms" -> new SMSNotification();
            default -> throw new IllegalArgumentException("Loại thông báo không hợp lệ: " + type);
        };
    }
}