package com.fifty.fifty.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fifty.fifty.domain.Profile;
import com.fifty.fifty.service.FilesServiceImpl;
import com.fifty.fifty.service.ProfileServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;



@RestController
@RequestMapping("/profile")
@Slf4j
public class ProfileController {

    @Autowired
    private ProfileServiceImpl profileServiceImpl;

    @GetMapping
    public ResponseEntity getMethodName() throws Exception {
        List<Profile> list = profileServiceImpl.list();

        Map<String, Object> result = new HashMap<>();
        log.info(list+  "이거ㅗ나옴?");
        result.put("list", list);

        return ResponseEntity.ok(result);

        
    }

  @PutMapping()
        public ResponseEntity<?> updateProfile(@RequestBody Profile request) {
            try {
                System.out.println("왜안나오나요??" + request);
       
                // 프로필 업데이트 처리
                int result = profileServiceImpl.update(request);

                return ResponseEntity.ok(result);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("프로필 수정 실패");
            }
        }


    
}
