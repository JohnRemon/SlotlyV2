package com.example.SlotlyV2.feature.auth;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {

    List<VerificationToken> findAllByTokenTypeAndUsedAtIsNull(TokenType tokenType);

    @Modifying
    @Query("DELETE FROM VerificationToken t WHERE t.expiresAt <= :now")
    int deleteAllExpiredTokens(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE VerificationToken t SET t.usedAt = :now WHERE t.user.id = :userId AND t.tokenType = :tokenType AND t.usedAt IS NULL")
    int invalidateAllUserTokens(@Param("userId") Long userId, @Param("tokenType") TokenType tokenType,
            @Param("now") LocalDateTime now);
}
