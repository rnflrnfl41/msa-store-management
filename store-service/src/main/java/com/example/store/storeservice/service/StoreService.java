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

    public StoreDto getStore(UUID publicId) {
        Store store = getStoreByPublicId(publicId);
        return modelMapper.map(store, StoreDto.class);
    }

    public void updateStore(UUID publicId, StoreDto storeDto) {
        Store store = getStoreByPublicId(publicId);
        store.setName(storeDto.getName());
        store.setOwnerName(storeDto.getOwnerName());
        store.setPhone(storeDto.getPhone());
        storeRepository.save(store);
    }

    @Transactional
    public void deleteStore(UUID publicId) {
        try {
            log.info("Store 삭제 시작: {}", publicId);
            
            // 외부 서비스 정리는 다른 클래스에 위임
            externalServiceOrchestrator.deleteRelatedData(publicId);
            
            // Store 삭제만 담당
            storeRepository.deleteByPublicId(publicId);
            
            log.info("Store 삭제 완료: {}", publicId);
            
        } catch (Exception e) {
            log.error("Store 삭제 실패: {}", publicId, e);
            
            // 별도 서비스로 에러 로그 저장
            errorLogService.internalDeleteErrorLogSave(publicId, e);
            
            throw new CommonException(CommonExceptionCode.STORE_DELETION_FAILED);
        }
    }

    private Store getStoreByPublicId(UUID publicId) {
        return storeRepository.findByPublicId(publicId)
                .orElseThrow(() -> new CommonException(CommonExceptionCode.STORE_NOT_FOUND));
    }

    public int getTotalStoreCount() {
        return storeRepository.getTotalStoreCount();
    }
}
