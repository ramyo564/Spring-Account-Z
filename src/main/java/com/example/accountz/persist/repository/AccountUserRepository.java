package com.example.accountz.persist.repository;

import com.example.accountz.persist.entity.AccountUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountUserRepository
        extends JpaRepository<AccountUserEntity, Long> {
}
