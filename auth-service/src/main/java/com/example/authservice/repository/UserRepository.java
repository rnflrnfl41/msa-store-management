package com.example.authservice.repository;

import com.example.authservice.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository <User, UUID> {

    Optional<User> findByLoginId(String loginId);

    List<User> findByStoreId(Integer storeId);

    void deleteByStoreId(Integer storeId);
    
    // 특정 스토어의 유저 수 조회
    long countByStoreId(Integer storeId);
    
    // 전체 시스템의 유저 수 조회 (JPQL 사용)
    @Query("SELECT COUNT(u) FROM User u where u.role = 'ROLE_USER' ")
    int getTotalUserCount();
}
