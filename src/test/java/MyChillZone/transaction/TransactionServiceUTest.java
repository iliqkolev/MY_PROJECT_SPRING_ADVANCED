//package MyChillZone.transaction;
//
//import MyChillZone.exception.DomainException;
//import MyChillZone.notification.client.dto.NotificationRequest;
//import MyChillZone.notification.client.service.NotificationService;
//import MyChillZone.transaction.model.Transaction;
//import MyChillZone.transaction.model.TransactionStatus;
//import MyChillZone.transaction.model.TransactionType;
//import MyChillZone.transaction.model.repository.TransactionRepository;
//import MyChillZone.transaction.model.service.TransactionService;
//import MyChillZone.user.model.User;
//import MyChillZone.user.model.UserRole;
//import MyChillZone.wallet.model.Wallet;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.Currency;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class TransactionServiceUTest {
//
//    @Mock
//    private NotificationService notificationService;
//
//    @Mock
//    private TransactionRepository transactionRepository;
//
//    @InjectMocks
//    private TransactionService transactionService;
//
//    //CreateNewTransaction
//    @Test
//    void givenExistingUser_whenMakeTransaction(){
//
//        User user = User.builder()
//                .id(UUID.randomUUID())
//                .username("Ichkata")
//                .userRole(UserRole.USER)
//                .isActive(true)
//                .build();
//
//        Transaction expectedTransaction = Transaction.builder()
//                .owner(user)
//                .sender(String.valueOf(UUID.randomUUID()))
//                .receiver("MyChillZone")
//                .amount(BigDecimal.valueOf(200))
//                .balanceLeft(BigDecimal.valueOf(180))
//                .currency(Currency.getInstance("EUR"))
//                .type(TransactionType.PAYMENT)
//                .status(TransactionStatus.SUCCEEDED)
//                .description("description")
//                .failureReason("failureReason")
//                .build();
//
//        //WHEN
//        when(transactionRepository.save(any())).thenReturn(expectedTransaction);
//
//        transactionService.createNewTransaction(
//                user,
//                expectedTransaction.getSender(),
//                expectedTransaction.getReceiver(),
//                expectedTransaction.getAmount(),
//                expectedTransaction.getBalanceLeft(),
//                expectedTransaction.getCurrency(),
//                expectedTransaction.getType(),
//                expectedTransaction.getStatus(),
//                expectedTransaction.getDescription(),
//                expectedTransaction.getFailureReason()
//        );
//
//        //THEN
//        String emailBody = "%s transaction was successfully processed for you with amount %.2f EUR".formatted(expectedTransaction.getType(), expectedTransaction.getAmount());
//        verify(notificationService, times(1)).sendNotification(user.getId(), "MyChillZone Transaction", emailBody);
//
//        verify(transactionRepository, times(1)).save(any());
//    }
//
//    //getAllByOwnerId
//    @Test
//    void givenExistingOwnerId_whenGetAllByOwnerId_thenReturnTransactionsSortedByDateDesc() {
//
//        UUID ownerId = UUID.randomUUID();
//
//        Transaction transaction1 = Transaction.builder()
//                .id(UUID.randomUUID())
//                .owner(User.builder().id(ownerId).build())
//                .createdOn(LocalDateTime.now().minusDays(1))
//                .build();
//
//        Transaction transaction2 = Transaction.builder()
//                .id(UUID.randomUUID())
//                .owner(User.builder().id(ownerId).build())
//                .createdOn(LocalDateTime.now())
//                .build();
//
//        List<Transaction> mockTransactions = List.of(transaction2, transaction1);
//
//        when(transactionRepository.findAllByOwnerIdOrderByCreatedOnDesc(ownerId))
//                .thenReturn(mockTransactions);
//
//        List<Transaction> result = transactionService.getAllByOwnerId(ownerId);
//
//        assertEquals(2, result.size()); // Проверяваме броя на транзакциите
//        assertEquals(transaction2, result.get(0)); // Проверяваме, че първата транзакция е по-новата
//        assertEquals(transaction1, result.get(1)); // Проверяваме, че втората транзакция е по-старата
//
//        verify(transactionRepository, times(1))
//                .findAllByOwnerIdOrderByCreatedOnDesc(ownerId);
//    }
//
//    @Test
//    void givenNonExistingOwnerId_whenGetAllByOwnerId_thenReturnEmptyList() {
//
//        UUID nonExistingOwnerId = UUID.randomUUID();
//
//        when(transactionRepository.findAllByOwnerIdOrderByCreatedOnDesc(nonExistingOwnerId))
//                .thenReturn(List.of());
//
//        List<Transaction> result = transactionService.getAllByOwnerId(nonExistingOwnerId);
//
//        assertTrue(result.isEmpty());
//        verify(transactionRepository, times(1))
//                .findAllByOwnerIdOrderByCreatedOnDesc(nonExistingOwnerId);
//    }
//
//
//    //getById
//    @Test
//    void givenExistingTransactionId_whenGetById_thenReturnTransaction() {
//
//        UUID transactionId = UUID.randomUUID();
//
//        Transaction mockTransaction = Transaction.builder()
//                .id(transactionId)
//                .build();
//
//        when(transactionRepository.findById(transactionId))
//                .thenReturn(Optional.of(mockTransaction));
//
//        Transaction result = transactionService.getById(transactionId);
//
//        assertNotNull(result);
//        assertEquals(transactionId, result.getId());
//        verify(transactionRepository, times(1)).findById(transactionId);
//    }
//
//    @Test
//    void givenNonExistingTransactionId_whenGetById_thenThrowDomainException() {
//
//        UUID nonExistingId = UUID.randomUUID();
//        when(transactionRepository.findById(nonExistingId)).thenReturn(Optional.empty());
//
//        assertThrows(DomainException.class, () -> transactionService.getById(nonExistingId));
//
//        verify(transactionRepository, times(1)).findById(nonExistingId);
//    }
//
//    //getLastFourTransactionsByWallet
//    @Test
//    void shouldReturnLastFourSuccessfulTransactions() {
//
//        UUID ownerId = UUID.randomUUID();
//        UUID walletId = UUID.randomUUID();
//
//        Wallet wallet = Wallet.builder()
//                .id(walletId)
//                .owner(User.builder().id(ownerId).build())
//                .build();
//
//        Transaction success1 = Transaction.builder()
//                .id(UUID.randomUUID())
//                .sender(walletId.toString())
//                .owner(User.builder().id(ownerId).build())
//                .status(TransactionStatus.SUCCEEDED)
//                .createdOn(LocalDateTime.now().minusDays(1))
//                .build();
//
//        Transaction success2 = Transaction.builder()
//                .id(UUID.randomUUID())
//                .sender(walletId.toString())
//                .owner(User.builder().id(ownerId).build())
//                .status(TransactionStatus.SUCCEEDED)
//                .createdOn(LocalDateTime.now())
//                .build();
//
//        Transaction failed = Transaction.builder()
//                .id(UUID.randomUUID())
//                .sender(walletId.toString())
//                .owner(User.builder().id(ownerId).build())
//                .status(TransactionStatus.FAILED)
//                .createdOn(LocalDateTime.now().minusHours(1))
//                .build();
//
//        when(transactionRepository.findAllBySenderOrReceiverOrderByCreatedOnDesc(
//                walletId.toString(), walletId.toString()))
//                .thenReturn(List.of(success2, failed, success1));
//
//        List<Transaction> result = transactionService.getLastFourTransactionsByWallet(wallet);
//
//        assertEquals(2, result.size());
//        assertTrue(result.stream().allMatch(t -> t.getStatus() == TransactionStatus.SUCCEEDED));
//        assertEquals(success2, result.get(0));
//    }
//
//    @Test
//    void shouldReturnEmptyListForNoTransactions() {
//
//        Wallet wallet = Wallet.builder()
//                .id(UUID.randomUUID())
//                .owner(User.builder().id(UUID.randomUUID()).build())
//                .build();
//
//        when(transactionRepository.findAllBySenderOrReceiverOrderByCreatedOnDesc(
//                anyString(), anyString()))
//                .thenReturn(List.of());
//
//        List<Transaction> result = transactionService.getLastFourTransactionsByWallet(wallet);
//
//        assertTrue(result.isEmpty());
//    }
//
//
//
//
//}
