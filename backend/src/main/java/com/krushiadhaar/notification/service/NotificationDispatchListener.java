package com.krushiadhaar.notification.service;
import com.krushiadhaar.notification.entity.Notification;
import com.krushiadhaar.notification.provider.PushNotificationProvider;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class NotificationDispatchListener {
    private static final Logger logger = LoggerFactory.getLogger(NotificationDispatchListener.class);
    private final PushNotificationProvider pushProvider;

    public NotificationDispatchListener(PushNotificationProvider pushProvider) {
        this.pushProvider = pushProvider;
    }

    // Executes ONLY if the parent business transaction successfully COMMITS
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationCreatedEvent(NotificationCreatedEvent event) {
        Notification notification = event.getNotification();
        try {
            pushProvider.sendPush(notification);
        } catch (Exception ex) {
            // Log failure, DO NOT throw exception as the parent transaction has already committed safely
            logger.error("Failed to send push notification {} via Provider", notification.getId(), ex);
        }
    }
}
