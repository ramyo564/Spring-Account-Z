package com.example.accountz.persist.repository;

import com.example.accountz.persist.entity.AccountEntity;
import com.example.accountz.persist.entity.TransactionEntity;
import com.example.accountz.type.TransactionResultType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository
    extends JpaRepository<TransactionEntity, Long> {

  Optional<TransactionEntity> findByTransactionId(String transactionId);

  // 최근 날짜 순 정렬 (기본)(출금)
  List<TransactionEntity> findByUser_IdOrderByTransactedAtDesc(Long id);

  // 입금 정렬 (최근 날짜순)
  List<TransactionEntity> findByReceiverAccount_IdOrderByTransactedAtDesc(
      Long id);


  // 이름 정렬 (가나다순)
  List<TransactionEntity> findByUser_IdOrderByReceiverAccount_User_NameAsc(
      Long id);

  // 지정 날짜 조회
  List<TransactionEntity> findByTransactedAtBetweenAndUser_Id(
      LocalDateTime transactedAtStart, LocalDateTime transactedAtEnd,
      Long id);

  // 실패한 거래
  List<TransactionEntity> findByUser_IdAndTransactionResultType(
      Long userId, TransactionResultType resultType);

}
