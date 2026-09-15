package com.krushiadhaar.notification.service;
import com.krushiadhaar.notification.entity.Notification;
import org.springframework.context.ApplicationEvent;

public class NotificationCreatedEvent extends ApplicationEvent {
    private final Notification notification;
    
    public NotificationCreatedEvent(Object source, Notification notification) {
        super(source);
        this.notification = notification;
    }
    
    public Notification getNotification() { return notification; }
}
