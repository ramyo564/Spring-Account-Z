package com.example.accountz.persist.entity;

import com.example.accountz.type.TransactionResultType;
import com.example.accountz.type.TransactionType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "transactions")
public class TransactionEntity {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private UserEntity userId;

  @ManyToOne
  private AccountEntity account;
  private Long amount;
  private Long balanceSnapshot;

  @CreatedDate
  private LocalDateTime createdAt;

  private String transactionId;
  private LocalDateTime transactedAt;

  @Enumerated(EnumType.STRING)
  private TransactionType transactionType;
  @Enumerated(EnumType.STRING)
  private TransactionResultType transactionResultType;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TransactionEntity that = (TransactionEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}