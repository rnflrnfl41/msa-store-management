package com.example.authservice.repository;

import com.example.authservice.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    void deleteByUser_Id(UUID userId);

    Optional<RefreshToken> findByUser_Id(UUID id);

    Optional<RefreshToken> findByToken(String token);
}
