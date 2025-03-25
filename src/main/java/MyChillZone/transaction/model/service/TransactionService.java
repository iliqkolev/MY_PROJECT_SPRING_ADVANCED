package MyChillZone.transaction.model.service;

import MyChillZone.exception.DomainException;
import MyChillZone.transaction.model.Transaction;
import MyChillZone.transaction.model.TransactionStatus;
import MyChillZone.transaction.model.TransactionType;
import MyChillZone.transaction.model.repository.TransactionRepository;
import MyChillZone.user.model.User;
import MyChillZone.wallet.model.Wallet;
import MyChillZone.wallet.model.service.WalletService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction createNewTransaction(User owner, String sender, String receiver, BigDecimal transactionAmount, BigDecimal balanceLeft, Currency currency, TransactionType type, TransactionStatus status, String transactionDescription, String failureReason) {

        Transaction transaction = Transaction.builder()
                .owner(owner)
                .sender(sender)
                .receiver(receiver)
                .amount(transactionAmount)
                .balanceLeft(balanceLeft)
                .currency(currency)
                .type(type)
                .status(status)
                .description(transactionDescription)
                .failureReason(failureReason)
                .createdOn(LocalDateTime.now())
                .build();

        String emailBody = "%s transaction was successfully processed for you with amount %.2f EUR".formatted(transaction.getType(), transaction.getAmount());
//        notificationService.sendNotification(transaction.getOwner().getId(), "New Smart Wallet Transaction", emailBody);

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllByOwnerId(UUID ownerId) {
        return transactionRepository.findAllByOwnerIdOrderByCreatedOnDesc(ownerId);
    }

    public Transaction getById(UUID id) {
        return transactionRepository.findById(id).orElseThrow(() -> new DomainException("Transaction with id [%s] does not exist.".formatted(id)));
    }


    public List<Transaction> getLastFourTransactionsByWallet(Wallet wallet) {

        return transactionRepository.findAllBySenderOrReceiverOrderByCreatedOnDesc(wallet.getId().toString(), wallet.getId().toString())
                .stream()
                .filter(t -> t.getOwner().getId() == wallet.getOwner().getId())
                .filter(t -> t.getStatus() == TransactionStatus.SUCCEEDED)
                .limit(4)
                .collect(Collectors.toList());
    }


}
