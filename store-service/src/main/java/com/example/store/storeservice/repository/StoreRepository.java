package com.example.store.storeservice.repository;

import com.example.store.storeservice.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface StoreRepository extends JpaRepository <Store, Integer> {

    Optional<Store> findByPublicId(UUID publicId);

    void deleteByPublicId(UUID publicId);

    @Query("SELECT COUNT(s) FROM Store s")
    int getTotalStoreCount();
}
