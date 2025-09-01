package com.example.store.storeservice.service;

import com.example.exception.CommonException;
import com.example.exception.CommonExceptionCode;
import com.example.store.storeservice.dto.StoreCreateRequest;
import com.example.store.storeservice.dto.StoreDto;
import com.example.store.storeservice.entity.Store;
import com.example.store.storeservice.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreService {

    private final ModelMapper modelMapper;
    private final StoreRepository storeRepository;
    private final ExternalServiceOrchestrator externalServiceOrchestrator;
    private final ErrorLogService errorLogService;

    public void createStore(StoreCreateRequest request) {
        Store store = modelMapper.map(request, Store.class);
        storeRepository.save(store);
    }

    public List<StoreDto> getAllStore() {
        List<Store> storeList = storeRepository.findAll();
        return storeList.stream()
                .map(store -> modelMapper.map(store, StoreDto.class))
                .toList();
    }

    public StoreDto getStore(int storeId) {
        Store store = getStoreByPublicId(storeId);
        return modelMapper.map(store, StoreDto.class);
    }

    public void updateStore(int storeId, StoreDto storeDto) {
        Store store = getStoreByPublicId(storeId);
        store.setName(storeDto.getName());
        store.setOwnerName(storeDto.getOwnerName());
        store.setPhone(storeDto.getPhone());
        storeRepository.save(store);
    }

    @Transactional
    public void deleteStore(int storeId) {
        try {
            log.info("Store 삭제 시작: {}", storeId);
            
            // 외부 서비스 정리는 다른 클래스에 위임
            externalServiceOrchestrator.deleteRelatedData(storeId);
            
            // Store 삭제만 담당
            storeRepository.deleteById(storeId);
            
            log.info("Store 삭제 완료: {}", storeId);
            
        } catch (Exception e) {
            log.error("Store 삭제 실패: {}", storeId, e);
            
            // 별도 서비스로 에러 로그 저장
            errorLogService.internalDeleteErrorLogSave(storeId, e);
            
            throw new CommonException(CommonExceptionCode.STORE_DELETION_FAILED);
        }
    }

    private Store getStoreByPublicId(int storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new CommonException(CommonExceptionCode.STORE_NOT_FOUND));
    }

    public int getTotalStoreCount() {
        return storeRepository.getTotalStoreCount();
    }
}
