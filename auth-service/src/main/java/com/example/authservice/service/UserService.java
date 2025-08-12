package com.example.authservice.service;

import com.example.authservice.dto.SignupDto;
import com.example.authservice.dto.UserDto;
import com.example.authservice.entity.user.Role;
import com.example.authservice.entity.user.User;
import com.example.authservice.repository.UserRepository;
import com.example.exception.CommonException;
import com.example.exception.CommonExceptionCode;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void createUser(SignupDto signupDto) {

        if(signupDto.getStoreId().equals("") || signupDto.getStoreId() == null){
            throw new CommonException(CommonExceptionCode.NO_STORE_ID);
        }

        userRepository.findByLoginId(signupDto.getLoginId()).ifPresent(user -> {
            throw new CommonException(CommonExceptionCode.DUPLICATE_LOGIN_ID);
        });

        signupDto.setPassword(passwordEncoder.encode(signupDto.getPassword()));
        User user = modelMapper.map(signupDto,User.class);
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);
    }

    public void deleteUser(UUID userId) {

        userRepository.deleteById(userId);

    }

    public List<UserDto> getAllUserInfoByStoreId(UUID storeId) {

        List<User> userList = userRepository.findByStoreId(storeId);

        return userList.stream()
                .map(user -> modelMapper.map(user,UserDto.class))
                .toList();

    }

    public void updateUser(UUID userId, UserDto userDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(CommonExceptionCode.USER_NOT_FOUND));

        //패스워드가 수정 되었다면 encode 뒤 수정
        if(userDto.getPassword() != null && !userDto.getPassword().equals("")){

            String encodedPassword = passwordEncoder.encode(userDto.getPassword());
            user.setPassword(encodedPassword);

        }

        //로그인 아이디가 수정 되었다면 같은 아이디가 있는지 확인 후 저장
        if(!userDto.getLoginId().equals(user.getLoginId())){

            userRepository.findByLoginId(userDto.getLoginId()).ifPresent(u -> {
                throw new CommonException(CommonExceptionCode.DUPLICATE_LOGIN_ID);
            });
            user.setLoginId(userDto.getLoginId());

        }


        user.setName(userDto.getName());

        userRepository.save(user);

    }


}
