package com.example.store.storeservice.service;

import com.example.store.storeservice.dto.StoreCreateRequest;
import com.example.store.storeservice.dto.StoreDto;
import com.example.store.storeservice.entity.Store;
import com.example.store.storeservice.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final ModelMapper modelMapper;
    private final StoreRepository storeRepository;

    public void createStore(StoreCreateRequest request) {

        Store store = modelMapper.map(request,Store.class);
        storeRepository.save(store);

    }

    public List<StoreDto> getAllStore() {

        List<Store> storeList = storeRepository.findAll();

        return storeList.stream()
                .map(store -> modelMapper.map(store, StoreDto.class))
                .toList();

    }
}
