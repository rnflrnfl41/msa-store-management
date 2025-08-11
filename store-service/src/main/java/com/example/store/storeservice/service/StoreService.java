package com.example.store.storeservice.service;

import com.example.exception.CommonException;
import com.example.exception.CommonExceptionCode;
import com.example.store.storeservice.dto.StoreCreateRequest;
import com.example.store.storeservice.dto.StoreDto;
import com.example.store.storeservice.entity.Store;
import com.example.store.storeservice.repository.StoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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

    public StoreDto getStore(UUID publicId) {

        Store store = GetStoreByPublicId(publicId);

        return modelMapper.map(store,StoreDto.class);

    }

    public void updateStore(UUID publicId, StoreDto storeDto) {

        Store store = GetStoreByPublicId(publicId);

        store.setName(storeDto.getName());
        store.setOwnerName(storeDto.getOwnerName());
        store.setPhone(storeDto.getPhone());

        storeRepository.save(store);

    }

    @Transactional
    public void deleteStore(UUID publicId) {

        storeRepository.deleteByPublicId(publicId);

    }

    private Store GetStoreByPublicId(UUID publicId){
        return storeRepository.findByPublicId(publicId)
                .orElseThrow(() -> new CommonException(CommonExceptionCode.STORE_NOT_FOUND));
    }
}
