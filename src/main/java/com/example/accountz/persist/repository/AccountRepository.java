package com.example.accountz.persist.repository;

import com.example.accountz.persist.entity.AccountEntity;
import com.example.accountz.persist.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository
    extends JpaRepository<AccountEntity, Long> {

  Integer countByUser(UserEntity user);

  Optional<AccountEntity> findFirstByOrderByIdDesc();

  Optional<AccountEntity> findByAccountNumber(String AccountNumber);

}
