package com.krushiadhaar.notification.provider;
import com.krushiadhaar.notification.entity.Notification;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MockPushNotificationProvider implements PushNotificationProvider {
    private static final Logger logger = LoggerFactory.getLogger(MockPushNotificationProvider.class);

    @Override
    public void sendPush(Notification notification) {
        // Mock FCM push
        logger.info("Mock FCM sending payload: { notificationId: {}, type: {}, resourceId: {} } to userId: {}",
                notification.getId(), notification.getType(), notification.getResourceId(), notification.getUserId());
    }
}
