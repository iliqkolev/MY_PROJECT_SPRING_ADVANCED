package MyChillZone.web.dto;

import MyChillZone.subscription.model.SubscriptionPeriod;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class UpgradeRequest {

    private SubscriptionPeriod subscriptionPeriod;

    private UUID walletId;

}
