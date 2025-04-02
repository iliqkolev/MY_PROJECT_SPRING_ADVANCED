package MyChillZone.notification.client.service;

import MyChillZone.notification.client.NotificationClient;
import MyChillZone.notification.client.dto.Notification;
import MyChillZone.notification.client.dto.NotificationPreference;
import MyChillZone.notification.client.dto.NotificationRequest;
import MyChillZone.notification.client.dto.UpsertNotificationPreference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class NotificationService {

    private final NotificationClient notificationClient;

    @Autowired
    public NotificationService(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    public void saveNotificationPreference(UUID userId, boolean isEmailEnabled, String email){

        UpsertNotificationPreference notificationPreference = UpsertNotificationPreference.builder()
                .userId(userId)
                .contactInfo(email)
                .type("EMAIL")
                .notificationEnabled(isEmailEnabled)
                .build();

        ResponseEntity<Void> httpResponse = notificationClient.upsertNotificationPreference(notificationPreference);

        if (!httpResponse.getStatusCode().is2xxSuccessful()){
            log.error("Cant save user preference with id=[%s]".formatted(userId));
        }
    }

    public NotificationPreference getNotificationPreference(UUID userId) {

        ResponseEntity<NotificationPreference> httpResponse = notificationClient.getUserPreference(userId);
        if (!httpResponse.getStatusCode().is2xxSuccessful()){
            throw new RuntimeException("Notification preference for user id=[%s] does not exist.".formatted(userId));
        }

        return httpResponse.getBody();
    }


    public List<Notification> getNotificationHistory(UUID userId) {

        ResponseEntity<List<Notification>> httpResponse = notificationClient.getNotificationHistory(userId);

        return httpResponse.getBody();
    }

    public void sendNotification(UUID userId, String emailSubject, String emailBody) {

        NotificationRequest notificationRequest = NotificationRequest.builder()
                .userId(userId)
                .subject(emailSubject)
                .body(emailBody)
                .build();


        ResponseEntity<Void> httpResponse;
        try {
            httpResponse = notificationClient.sendNotification(notificationRequest);
            if (!httpResponse.getStatusCode().is2xxSuccessful()){
                log.error("[Feign call to notification-svc failed] Can't send email to user with id = [%s]".formatted(userId));
            }
        }catch (Exception e){
            log.warn("Can't send email to user with id = [%s] due to 500 Internal Server Error.".formatted(userId));
        }
    }

    public void updateNotificationPreference(UUID userId, boolean enabled) {

        try {
        notificationClient.updateNotificationPreference(userId, enabled);
        }catch (Exception e){
            log.warn("Can't update notification preferences for user with id = [%s].".formatted(userId));
        }
    }
}
