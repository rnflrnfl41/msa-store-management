package com.example.authservice.repository;

import com.example.authservice.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository <User, UUID> {

    Optional<User> findByLoginId(String loginId);

    List<User> findByStoreId(UUID storeId);

    void deleteByStoreId(UUID storeId);

}
