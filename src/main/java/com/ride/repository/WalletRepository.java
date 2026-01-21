package com.ride.repository;

import com.ride.domain.Wallet;
import com.ride.domain.enums.WalletType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserIdAndType(Long userId, WalletType type);
}
