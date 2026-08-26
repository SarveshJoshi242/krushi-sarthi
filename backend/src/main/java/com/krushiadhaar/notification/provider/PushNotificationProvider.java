package com.krushiadhaar.notification.provider;
import com.krushiadhaar.notification.entity.Notification;
public interface PushNotificationProvider {
    void sendPush(Notification notification);
}
