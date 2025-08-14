package com.fifty.fifty.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fifty.fifty.domain.CustomUser;
import com.fifty.fifty.domain.Users;
import com.fifty.fifty.mapper.UserMapper;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info(":::::::::: UserDetailServiceImpl ::::::::::");
        log.info("- 사용자 정의 인증을 위해, 사용자 정보 조회");
        log.info("- username : " + username);

        Users user = null;
        try {
            // 👩‍💼 사용자 정보 및 권한 조회
            user = userMapper.select(username);
            log.info("사용자 정보 : " + user );
        } catch (Exception e) {
            e.printStackTrace();
        }
        if( user == null ) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다." + username);
        }

        // 🔐 CustomUser ➡ UserDetails
        CustomUser customUser = new CustomUser(user);
        return customUser;
    }
    
}