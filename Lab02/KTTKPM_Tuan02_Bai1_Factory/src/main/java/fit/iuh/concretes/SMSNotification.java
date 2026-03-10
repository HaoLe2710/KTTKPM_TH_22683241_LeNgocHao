package fit.iuh.concretes;

import fit.iuh.interfaces.Notification;

public class SMSNotification implements Notification {
    public void notifyUser() { System.out.println("Gửi tin nhắn SMS..."); }
}