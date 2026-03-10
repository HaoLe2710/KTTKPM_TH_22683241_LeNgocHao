package fit.iuh.concretes;

import fit.iuh.interfaces.Notification;

// Concrete Products
public class EmailNotification implements Notification {
    public void notifyUser() { System.out.println("Gửi email thông báo..."); }
}