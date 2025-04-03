package MyChillZone;

import MyChillZone.subscription.model.Subscription;
import MyChillZone.subscription.model.SubscriptionPeriod;
import MyChillZone.subscription.model.SubscriptionStatus;
import MyChillZone.subscription.model.SubscriptionType;
import MyChillZone.user.model.Country;
import MyChillZone.user.model.User;
import MyChillZone.user.model.UserRole;
import MyChillZone.wallet.model.Wallet;
import MyChillZone.wallet.model.WalletStatus;
import ch.qos.logback.core.util.Loader;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class TestBuilder {

    public static  User aRandomUser(){

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("User")
                .password("123123")
                .userRole(UserRole.USER)
                .country(Country.BULGARIA)
                .isActive(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        Wallet wallet = Wallet.builder()
                .id(UUID.randomUUID())
                .owner(user)
                .balance(BigDecimal.ZERO)
                .status(WalletStatus.ACTIVE)
                .currency(Currency.getInstance("EUR"))
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        Subscription subscription = Subscription.builder()
                .id(UUID.randomUUID())
                .type(SubscriptionType.PREMIUM)
                .price(BigDecimal.ZERO)
                .status(SubscriptionStatus.ACTIVE)
                .period(SubscriptionPeriod.MONTHLY)
                .createdOn(LocalDateTime.now())
                .completedOn(LocalDateTime.now())
                .owner(user)
                .renewalAllowed(true)
                .build();

        user.setSubscriptions(List.of(subscription));
        user.setWallets(List.of(wallet));

        return user;
    }



















}
