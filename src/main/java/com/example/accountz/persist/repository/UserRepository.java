package com.example.accountz.persist.repository;

import com.example.accountz.persist.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository
    extends JpaRepository<UserEntity, Long> {

  boolean existsByEmail(String email);

  Optional<UserEntity> findByEmail(String email);
}
