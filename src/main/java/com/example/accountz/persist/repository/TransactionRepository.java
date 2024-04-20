package com.example.accountz.persist.repository;

import com.example.accountz.persist.entity.TransactionEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository
    extends JpaRepository<TransactionEntity, Long> {

  Optional<TransactionEntity> findByTransactionId(String transactionId);
}
