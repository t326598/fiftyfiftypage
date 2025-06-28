package com.fifty.fifty.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fifty.fifty.domain.Months;
import com.fifty.fifty.domain.Profile;
import com.fifty.fifty.service.MonthServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/months")
@Slf4j
public class MonthController {
    
    @Autowired
    private MonthServiceImpl monthServiceImpl;

    @GetMapping("/list")
    public ResponseEntity<?> getMethodName() {
        List<Months> monthList = monthServiceImpl.calendarList();

        return new ResponseEntity<>(monthList, HttpStatus.OK);
    }
    


    @GetMapping("")
    public ResponseEntity<?> getMethodName(@RequestParam("month") int month) {
       try{
        Months url = monthServiceImpl.getBackgroundByMonth(month);
                return new ResponseEntity<>(url, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
      @PutMapping()
        public ResponseEntity<?> updateBackground(@RequestBody Months request) {
            try {
                System.out.println("왜안나오나요??" + request);

                // 프로필 업데이트 처리
                int result = monthServiceImpl.monthsUpdate(request);

                return ResponseEntity.ok(result);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("프로필 수정 실패");
            }
        }
    

}
