package com.example.store.storeservice.repository;

import com.example.store.storeservice.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository <Store, Integer> {
}
