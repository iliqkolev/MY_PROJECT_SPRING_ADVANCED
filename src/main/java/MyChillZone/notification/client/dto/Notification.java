package MyChillZone.notification.client.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Notification {

    private String subject;

    private LocalDateTime createdOn;

    private String status;

    private String type;
}
