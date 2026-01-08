package com.example.SlotlyV2.feature.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByDisplayName(String displayName);

    boolean existsByEmail(String email);

    boolean existsByDisplayName(String displayName);

    Optional<User> findByEmailVerificationToken(String token);

    Optional<User> findByPasswordVerificationToken(String token);

}
