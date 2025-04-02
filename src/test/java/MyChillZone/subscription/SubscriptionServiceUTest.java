package MyChillZone.subscription;


import MyChillZone.exception.DomainException;
import MyChillZone.subscription.model.Subscription;
import MyChillZone.subscription.model.SubscriptionPeriod;
import MyChillZone.subscription.model.SubscriptionStatus;
import MyChillZone.subscription.model.SubscriptionType;
import MyChillZone.subscription.model.repository.SubscriptionRepository;
import MyChillZone.subscription.model.service.SubscriptionService;
import MyChillZone.transaction.model.Transaction;
import MyChillZone.transaction.model.TransactionStatus;
import MyChillZone.user.model.User;
import MyChillZone.wallet.model.service.WalletService;
import MyChillZone.web.dto.UpgradeRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceUTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private SubscriptionService subscriptionService;


    //createDefaultSubscription
    @Test
    void givenExistingUser_whenRegister_thenGetNewSubscription(){

        User user = User.builder().id(UUID.randomUUID()).build();
        Subscription expectedSubscription = Subscription.builder()
                .owner(user)
                .status(SubscriptionStatus.ACTIVE)
                .period(SubscriptionPeriod.MONTHLY)
                .type(SubscriptionType.DEFAULT)
                .price(new BigDecimal("0"))
                .renewalAllowed(true)
                .createdOn(LocalDateTime.now())
                .completedOn(LocalDateTime.now())
                .build();

        when(subscriptionRepository.save(any()))
                .thenReturn(expectedSubscription);

        // Act
        Subscription result = subscriptionService.createDefaultSubscription(user);

        // Assert
        assertNotNull(result);
        assertEquals(SubscriptionPeriod.MONTHLY, result.getPeriod());
        assertEquals(SubscriptionType.DEFAULT, result.getType());
        assertEquals(SubscriptionStatus.ACTIVE, result.getStatus());
        assertEquals(BigDecimal.ZERO, result.getPrice());
        assertTrue(result.isRenewalAllowed());
        assertEquals(user, result.getOwner());

        verify(subscriptionRepository, times(1)).save(any(Subscription.class));
    }


    @Test
    void upgrade_ShouldSuccessfullyUpgradeMonthlyPremiumSubscription() {
        // Arrange
        User user = User.builder().id(UUID.randomUUID()).build();
        Subscription currentSub = Subscription.builder()
                .owner(user)
                .status(SubscriptionStatus.ACTIVE)
                .build();

        when(subscriptionRepository.findByStatusAndOwnerId(any(), any()))
                .thenReturn(Optional.of(currentSub));

        Transaction mockTransaction = new Transaction();
        mockTransaction.setStatus(TransactionStatus.SUCCEEDED);
        when(walletService.charge(any(), any(), any(), any()))
                .thenReturn(mockTransaction);

        // Act
        Transaction result = subscriptionService.upgrade(
                user,
                SubscriptionType.PREMIUM,
                UpgradeRequest.builder()
                        .walletId(UUID.randomUUID())
                        .subscriptionPeriod(SubscriptionPeriod.MONTHLY)
                        .build());


        // Assert
        assertEquals(TransactionStatus.SUCCEEDED, result.getStatus());

        verify(walletService).charge(
                eq(user),
                any(UUID.class),
                eq(new BigDecimal("19.99")),
                anyString()
        );

        verify(subscriptionRepository, times(2)).save(any(Subscription.class));
    }

    @Test
    void upgrade_ShouldThrowWhenNoActiveSubscription() {
        User user = User.builder().id(UUID.randomUUID()).build();
        when(subscriptionRepository.findByStatusAndOwnerId(any(), any()))
                .thenReturn(Optional.empty());

        assertThrows(DomainException.class, () ->
                subscriptionService.upgrade(user, SubscriptionType.PREMIUM,
                        UpgradeRequest.builder()
                                .walletId(UUID.randomUUID())
                                .subscriptionPeriod(SubscriptionPeriod.MONTHLY)
                                .build()
        ));

    }

    @Test
    void upgrade_ShouldReturnFailedTransactionWhenChargeFails() {
        User user = User.builder().id(UUID.randomUUID()).build();

        Subscription currentSubscription = Subscription.builder()
                .owner(user)
                .status(SubscriptionStatus.ACTIVE)
                .build();

        Transaction failedTransaction = Transaction.builder()
                .status(TransactionStatus.FAILED)
                .build();

        when(subscriptionRepository.findByStatusAndOwnerId(any(), any()))
                .thenReturn(Optional.of(currentSubscription));
        when(walletService.charge(any(), any(), any(), any()))
                .thenReturn(failedTransaction);

        Transaction result = subscriptionService.upgrade(user, SubscriptionType.PREMIUM,
                UpgradeRequest.builder()
                        .walletId(UUID.randomUUID())
                        .subscriptionPeriod(SubscriptionPeriod.MONTHLY)
                        .build());

        assertEquals(TransactionStatus.FAILED, result.getStatus());
    }





}
