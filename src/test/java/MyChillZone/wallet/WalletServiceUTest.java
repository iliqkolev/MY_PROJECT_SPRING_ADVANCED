//package MyChillZone.wallet;
//
//import MyChillZone.exception.DomainException;
//import MyChillZone.transaction.model.Transaction;
//import MyChillZone.transaction.model.TransactionStatus;
//import MyChillZone.transaction.model.TransactionType;
//import MyChillZone.transaction.model.service.TransactionService;
//import MyChillZone.user.model.User;
//import MyChillZone.wallet.model.Wallet;
//import MyChillZone.wallet.model.WalletStatus;
//import MyChillZone.wallet.model.repository.WalletRepository;
//import MyChillZone.wallet.model.service.WalletService;
//import MyChillZone.web.dto.PaymentNotificationEvent;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.context.ApplicationEventPublisher;
//
//import java.math.BigDecimal;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class WalletServiceUTest {
//    @Mock
//    private ApplicationEventPublisher eventPublisher;
//    @Mock
//    private TransactionService transactionService;
//
//    @Mock
//    private  WalletRepository walletRepository;
//
//    @InjectMocks
//    private WalletService walletService;
//
//
//    //createDefaultWallet
//    @Test
//    void createDefaultWallet_ShouldCreateNewWalletForUserWithoutWallet() {
//
//        User user = User.builder()
//                .id(UUID.randomUUID())
//                .username("Ichkata")
//                .build();
//
//        when(walletRepository.findAllByOwnerUsername(user.getUsername()))
//                .thenReturn(Collections.emptyList());
//
//        Wallet mockWallet = Wallet.builder()
//                .id(UUID.randomUUID())
//                .owner(user)
//                .balance(BigDecimal.ZERO)
//                .build();
//
//        when(walletRepository.save(any(Wallet.class)))
//                .thenReturn(mockWallet);
//
//        Wallet result = walletService.createDefaultWallet(user);
//
//
//        assertNotNull(result);
//        assertEquals(user, result.getOwner());
//        assertEquals(BigDecimal.ZERO, result.getBalance());
//
//        verify(walletRepository, times(1)).save(any(Wallet.class));
//
//    }
//
//    @Test
//    void createDefaultWallet_ShouldThrowWhenUserHasExistingWallet() {
//
//        User user = User.builder()
//                .id(UUID.randomUUID())
//                .username("existingUser")
//                .build();
//
//        Wallet existingWallet = Wallet.builder()
//                .id(UUID.randomUUID())
//                .owner(user)
//                .build();
//
//        when(walletRepository.findAllByOwnerUsername(user.getUsername()))
//                .thenReturn(List.of(existingWallet));
//
//
//         assertThrows(DomainException.class, () -> walletService.createDefaultWallet(user));
//
//        verify(walletRepository, never()).save(any());
//    }
//
//    @Test
//    void createDefaultWallet_ShouldLogCorrectBalanceFormat() {
//
//        User user = User.builder()
//                .id(UUID.randomUUID())
//                .username("newUser")
//                .build();
//
//        Wallet walletWithBalance = Wallet.builder()
//                .id(UUID.randomUUID())
//                .owner(user)
//                .balance(new BigDecimal("100.50"))
//                .build();
//
//        when(walletRepository.findAllByOwnerUsername(any()))
//                .thenReturn(Collections.emptyList());
//        when(walletRepository.save(any(Wallet.class)))
//                .thenReturn(walletWithBalance);
//
//        walletService.createDefaultWallet(user);
//    }
//
//    //getWalletById
//    @Test
//    void getWalletById_ShouldReturnWalletWhenExists() {
//
//        UUID walletId = UUID.randomUUID();
//        Wallet expectedWallet = Wallet.builder()
//                .id(walletId)
//                .balance(new BigDecimal("100.00"))
//                .build();
//
//        when(walletRepository.findById(walletId))
//                .thenReturn(Optional.of(expectedWallet));
//
//        Wallet result = walletService.getWalletById(walletId);
//
//        assertNotNull(result);
//        assertEquals(walletId, result.getId());
//        assertEquals(new BigDecimal("100.00"), result.getBalance());
//        verify(walletRepository).findById(walletId);
//    }
//
//    @Test
//    void getWalletById_ShouldThrowExceptionWhenWalletNotFound() {
//
//        UUID nonExistentWalletId = UUID.randomUUID();
//        when(walletRepository.findById(nonExistentWalletId))
//                .thenReturn(Optional.empty());
//
//        assertThrows(DomainException.class, () -> walletService.getWalletById(nonExistentWalletId));
//
//        verify(walletRepository).findById(nonExistentWalletId);
//    }
//
//    //getLastFourTransactions
//    @Test
//    void getLastFourTransactions_ShouldReturnMapWithTransactionsForEachWallet() {
//
//        UUID walletId1 = UUID.randomUUID();
//        UUID walletId2 = UUID.randomUUID();
//
//        Wallet wallet1 = Wallet.builder().id(walletId1).build();
//        Wallet wallet2 = Wallet.builder().id(walletId2).build();
//
//        Transaction t1 = Transaction.builder().id(UUID.randomUUID()).amount(new BigDecimal("100.00")).build();
//        Transaction t2 = Transaction.builder().id(UUID.randomUUID()).amount(new BigDecimal("200.00")).build();
//        Transaction t3 = Transaction.builder().id(UUID.randomUUID()).amount(new BigDecimal("300.00")).build();
//        Transaction t4 = Transaction.builder().id(UUID.randomUUID()).amount(new BigDecimal("400.00")).build();
//
//        when(transactionService.getLastFourTransactionsByWallet(wallet1))
//                .thenReturn(List.of(t1, t2));
//        when(transactionService.getLastFourTransactionsByWallet(wallet2))
//                .thenReturn(List.of(t3, t4));
//
//        Map<UUID, List<Transaction>> result = walletService.getLastFourTransactions(
//                List.of(wallet1, wallet2)
//        );
//
//        assertEquals(2, result.size());
//        assertEquals(2, result.get(walletId1).size());
//        assertEquals(2, result.get(walletId2).size());
//        assertEquals(new BigDecimal("100.00"), result.get(walletId1).get(0).getAmount());
//    }
//
//    @Test
//    void getLastFourTransactions_ShouldReturnEmptyMapForEmptyInput() {
//
//        Map<UUID, List<Transaction>> result = walletService.getLastFourTransactions(
//                Collections.emptyList()
//        );
//
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    void getLastFourTransactions_ShouldHandleWalletWithNoTransactions() {
//
//        UUID walletId = UUID.randomUUID();
//        Wallet wallet = Wallet.builder().id(walletId).build();
//
//        when(transactionService.getLastFourTransactionsByWallet(wallet))
//                .thenReturn(Collections.emptyList());
//
//        Map<UUID, List<Transaction>> result = walletService.getLastFourTransactions(
//                List.of(wallet)
//        );
//
//        assertEquals(1, result.size());
//        assertTrue(result.get(walletId).isEmpty());
//    }
//
//    //SwitchStatus
//    @Test
//    void switchStatus_ShouldActivateInactiveWallet() {
//
//        UUID ownerId = UUID.randomUUID();
//        UUID walletId = UUID.randomUUID();
//
//        Wallet wallet = Wallet.builder()
//                .id(walletId)
//                .owner(User.builder().id(ownerId).build())
//                .status(WalletStatus.INACTIVE)
//                .build();
//
//        when(walletRepository.findByIdAndOwnerId(walletId, ownerId))
//                .thenReturn(Optional.of(wallet));
//
//        walletService.switchStatus(walletId, ownerId);
//
//        verify(walletRepository, times(1)).save(wallet);
//        assertEquals(WalletStatus.ACTIVE, wallet.getStatus());
//    }
//
//    @Test
//    void switchStatus_ShouldDeactivateActiveWallet() {
//
//        UUID ownerId = UUID.randomUUID();
//        UUID walletId = UUID.randomUUID();
//
//        Wallet wallet = Wallet.builder()
//                .id(walletId)
//                .owner(User.builder().id(ownerId).build())
//                .status(WalletStatus.ACTIVE)
//                .build();
//
//        when(walletRepository.findByIdAndOwnerId(walletId, ownerId))
//                .thenReturn(Optional.of(wallet));
//
//        walletService.switchStatus(walletId, ownerId);
//
//        verify(walletRepository, times(1)).save(wallet);
//        assertEquals(WalletStatus.INACTIVE, wallet.getStatus());
//    }
//
//    @Test
//    void switchStatus_ShouldThrowWhenWalletNotFound() {
//
//        UUID walletId = UUID.randomUUID();
//        UUID ownerId = UUID.randomUUID();
//
//        when(walletRepository.findByIdAndOwnerId(walletId, ownerId))
//                .thenReturn(Optional.empty());
//
//       assertThrows(DomainException.class, () -> walletService.switchStatus(walletId, ownerId));
//
//        verify(walletRepository, never()).save(any());
//    }
//
//    //topUp
//    @Test
//    void topUp_ShouldSuccessfullyTopUpActiveWallet() {
//
//        UUID walletId = UUID.randomUUID();
//        BigDecimal amount = new BigDecimal("100.00");
//        BigDecimal initialBalance = new BigDecimal("500.00");
//
//        Wallet wallet = Wallet.builder()
//                .id(walletId)
//                .owner(User.builder().id(UUID.randomUUID()).build())
//                .balance(initialBalance)
//                .currency(Currency.getInstance("USD"))
//                .status(WalletStatus.ACTIVE)
//                .build();
//
//        when(walletRepository.findById(walletId))
//                .thenReturn(Optional.of(wallet));
//
//        Transaction expectedTransaction = new Transaction();
//        when(transactionService.createNewTransaction(
//                any(), any(), any(), any(), any(), any(),
//                eq(TransactionType.DEPOSIT),
//                eq(TransactionStatus.SUCCEEDED),
//                anyString(),
//                isNull()))
//                .thenReturn(expectedTransaction);
//
//        Transaction result = walletService.topUp(walletId, amount);
//
//        assertSame(expectedTransaction, result);
//        assertEquals(initialBalance.add(amount), wallet.getBalance());
//        verify(walletRepository, times(1)).save(wallet);
//    }
//
//    @Test
//    void topUp_ShouldFailForInactiveWallet() {
//
//        UUID walletId = UUID.randomUUID();
//        BigDecimal amount = new BigDecimal("50.00");
//
//        Wallet wallet = Wallet.builder()
//                .id(walletId)
//                .owner(User.builder().id(UUID.randomUUID()).build())
//                .status(WalletStatus.INACTIVE)
//                .build();
//
//        when(walletRepository.findById(walletId))
//                .thenReturn(Optional.of(wallet));
//
//        Transaction expectedTransaction = new Transaction();
//        when(transactionService.createNewTransaction(
//                any(), any(), any(), any(), any(), any(),
//                eq(TransactionType.DEPOSIT),
//                eq(TransactionStatus.FAILED),
//                anyString(),
//                eq("Inactive wallet")))
//                .thenReturn(expectedTransaction);
//
//        Transaction result = walletService.topUp(walletId, amount);
//
//        assertSame(expectedTransaction, result);
//        verify(walletRepository, never()).save(any());
//    }
//
//    @Test
//    void topUp_ShouldThrowExceptionForNegativeAmountAndMissingWallet() {
//
//        UUID walletId = UUID.randomUUID();
//        BigDecimal negativeAmount = new BigDecimal("-100.00");
//
//        assertThrows(DomainException.class, () -> walletService.topUp(walletId, negativeAmount));
//    }
//
//    //Charge
//    @Test
//    void charge_ShouldSuccessfullyChargeActiveWalletWithSufficientFunds() {
//
//        UUID walletId = UUID.randomUUID();
//        User user = User.builder().id(UUID.randomUUID()).email("test@example.com").build();
//        BigDecimal amount = new BigDecimal("50.00");
//        BigDecimal initialBalance = new BigDecimal("100.00");
//
//        Wallet wallet = Wallet.builder()
//                .id(walletId)
//                .balance(initialBalance)
//                .currency(Currency.getInstance("BGN"))
//                .status(WalletStatus.ACTIVE)
//                .build();
//
//        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
//
//        Transaction mockTransaction = new Transaction();
//        when(transactionService.createNewTransaction(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
//                .thenReturn(mockTransaction);
//
//        Transaction result = walletService.charge(user, walletId, amount, "Тест плащане");
//
//        assertEquals(initialBalance.subtract(amount), wallet.getBalance());
//        assertNotNull(wallet.getUpdatedOn());
//        verify(walletRepository).save(wallet);
//        verify(eventPublisher).publishEvent(any(PaymentNotificationEvent.class));
//    }
//
//    @Test
//    void charge_ShouldFailForInactiveWallet() {
//
//        UUID walletId = UUID.randomUUID();
//        Wallet wallet = Wallet.builder()
//                .id(walletId)
//                .balance(new BigDecimal("100.00"))
//                .status(WalletStatus.INACTIVE)
//                .build();
//
//        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
//
//        walletService.charge(new User(), walletId, new BigDecimal("10.00"), "Тест");
//
//        verify(transactionService).createNewTransaction(
//                any(User.class),
//                eq(walletId.toString()),
//                eq("MyChillZone"),
//                eq(new BigDecimal("10.00")),
//                eq(new BigDecimal("100.00")),
//                isNull(),
//                eq(TransactionType.PAYMENT),
//                eq(TransactionStatus.FAILED),
//                eq("Тест"),
//                eq("Inactive wallet status"));
//        verify(walletRepository, never()).save(any());
//    }
//
//    @Test
//    void charge_ShouldFailForInsufficientFunds() {
//
//        UUID walletId = UUID.randomUUID();
//        Wallet wallet = Wallet.builder()
//                .id(walletId)
//                .balance(new BigDecimal("50.00"))
//                .status(WalletStatus.ACTIVE)
//                .build();
//
//        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
//
//        walletService.charge(new User(), walletId, new BigDecimal("100.00"), "Тест");
//
//        verify(transactionService).createNewTransaction(
//                any(), any(), any(), any(), any(), any(),
//                eq(TransactionType.PAYMENT),
//                eq(TransactionStatus.FAILED),
//                anyString(),
//                eq("Insufficient funds"));
//    }
//
//
//
//}
