package com.krushiadhaar.notification.service;
import com.krushiadhaar.notification.entity.Notification;
import com.krushiadhaar.notification.repository.NotificationRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher eventPublisher;

    public NotificationService(NotificationRepository notificationRepository, ApplicationEventPublisher eventPublisher) {
        this.notificationRepository = notificationRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void createAndPublish(Notification notification) {
        // Step 1: Commit the notification to the DB during the active business transaction
        Notification saved = notificationRepository.save(notification);
        
        // Step 2: Publish the internal Spring ApplicationEvent
        eventPublisher.publishEvent(new NotificationCreatedEvent(this, saved));
    }
}
