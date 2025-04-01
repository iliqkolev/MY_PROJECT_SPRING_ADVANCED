package MyChillZone.notification.client.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class NotificationPreference {

    private String type;

    private boolean enabled;

    private String contactInfo;
}
