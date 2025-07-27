package com.example.authservice.repository;

import com.example.authservice.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface UserInfoRepository extends JpaRepository <UserInfo, Integer> {

    Optional<UserInfo> findByLoginId(String loginId);
}
