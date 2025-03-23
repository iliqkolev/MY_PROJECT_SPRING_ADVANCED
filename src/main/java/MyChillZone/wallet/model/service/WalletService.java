package MyChillZone.wallet.model.service;

import MyChillZone.exception.DomainException;
import MyChillZone.transaction.model.Transaction;
import MyChillZone.transaction.model.TransactionStatus;
import MyChillZone.transaction.model.TransactionType;
import MyChillZone.transaction.model.service.TransactionService;
import MyChillZone.user.model.User;
import MyChillZone.wallet.model.Wallet;
import MyChillZone.wallet.model.WalletStatus;
import MyChillZone.wallet.model.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class WalletService {
    private final static String MY_CHILL_ZONE="MyChillZone";
    private final WalletRepository walletRepository;
    private final TransactionService transactionService;

    @Autowired
    public WalletService(WalletRepository walletRepository, TransactionService transactionService) {
        this.walletRepository = walletRepository;
        this.transactionService = transactionService;
    }

    public Wallet createDefaultWallet(User user) {
        List<Wallet> allUserWallets = walletRepository.findAllByOwnerUsername(user.getUsername());
        if (!allUserWallets.isEmpty()){
            throw  new DomainException("User with id [%s] already has a wallet".formatted(user.getId()));
        }

        Wallet wallet = walletRepository.save(initializeWallet(user));

        log.info("Successfully created new wallet with id [%s] and balance [%.2f]".formatted(wallet.getId(), wallet.getBalance()));

        return wallet;
    }

    private Wallet initializeWallet(User user) {
        return  Wallet.builder()
                .owner(user)
                .status(WalletStatus.ACTIVE)
                .balance(new BigDecimal("20.00"))
                .currency(Currency.getInstance("EUR"))
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }

    @Transactional
    public Transaction charge(User user, UUID walletId, BigDecimal amount, String description){

        Wallet wallet = getWalletById(walletId);

        String failureReason = null;
        boolean isFailedTransaction = false;

        if (wallet.getStatus() == WalletStatus.INACTIVE){
            failureReason = "Inactive wallet status";
            isFailedTransaction = true;
        }

        if (wallet.getBalance().compareTo(amount) < 0){
            failureReason = "Insufficient funds";
            isFailedTransaction = true;
        }

        if (isFailedTransaction){
            return  transactionService.createNewTransaction(
                    user,
                    wallet.getId().toString(),
                    MY_CHILL_ZONE,
                    amount,
                    wallet.getBalance(),
                    wallet.getCurrency(),
                    TransactionType.PAYMENT,
                    TransactionStatus.FAILED,
                    description,
                    failureReason
            );
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setUpdatedOn(LocalDateTime.now());

        walletRepository.save(wallet);

        return transactionService.createNewTransaction(
                user,
                wallet.getId().toString(),
                MY_CHILL_ZONE,
                amount,
                wallet.getBalance(),
                wallet.getCurrency(),
                TransactionType.PAYMENT,
                TransactionStatus.SUCCEEDED,
                description,
                null
        );
    }
    private Wallet getWalletById(UUID walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new DomainException("Wallet with id [%s] does not exist.".formatted(walletId)));
    }
}
