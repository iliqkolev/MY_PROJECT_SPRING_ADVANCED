package MyChillZone.wallet.model.repository;

import MyChillZone.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    List<Wallet> findAllByOwnerUsername(String username);
}
