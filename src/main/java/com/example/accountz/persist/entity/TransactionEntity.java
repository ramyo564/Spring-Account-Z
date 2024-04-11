package com.example.accountz.persist.entity;

import com.example.accountz.type.TransactionResultType;
import com.example.accountz.type.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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
}
