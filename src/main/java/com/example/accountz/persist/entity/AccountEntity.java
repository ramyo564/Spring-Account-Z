package com.example.accountz.persist.entity;

import com.example.accountz.exception.GlobalException;
import com.example.accountz.type.AccountStatus;
import com.example.accountz.type.ErrorCode;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Builder
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "accounts")
public class AccountEntity {

  @Id
  @GeneratedValue
  private Long id;

  @Enumerated(EnumType.STRING)
  private AccountStatus accountStatus;

  private String accountNumber;
  @Builder.Default
  private Long balance = 0L;

  private LocalDateTime registeredAt;
  private LocalDateTime unRegisteredAt;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @CreatedDate
  private LocalDateTime createdAt;
  @LastModifiedDate
  private LocalDateTime updatedAt;

  public void saveMoney(Long addMoney) {
    if (addMoney < 0) {
      throw new GlobalException(ErrorCode.NOT_MINUS_MONEY);
    }
    balance += addMoney;
  }

  public void useBalance(Long amount) {
    if (amount > balance) {
      throw new GlobalException(ErrorCode.AMOUNT_EXCEED_BALANCE);
    }
    balance -= amount;

  }

  public void cancelBalance(Long amount) {
    if (amount < 0) {
      throw new GlobalException(ErrorCode.INVALID_REQUEST);
    }
    balance += amount;

  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AccountEntity that = (AccountEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

}
