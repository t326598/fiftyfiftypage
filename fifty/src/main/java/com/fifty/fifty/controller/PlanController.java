package com.fifty.fifty.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fifty.fifty.domain.Plan;
import com.fifty.fifty.service.PlanServiceImpl;

import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/plan")
@Slf4j
public class PlanController {

    @Autowired
    private PlanServiceImpl planServiceImpl;

        @GetMapping
        public ResponseEntity<?> getAll() {
            try {
                List<Plan> planList = planServiceImpl.list();
                
                // FullCalendar에 맞게 가공
                List<Map<String, Object>> eventList = planList.stream().map(plan -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("no", plan.getNo());
                    map.put("crt", plan.getCrt());
                    map.put("title", plan.getTitle());
                    map.put("start", plan.getStartAt().toString());
                    map.put("end", plan.getEndAt() != null ? plan.getEndAt().toString() : null);
                    map.put("backgroundColor", getCategoryColor(plan.getCrt()));
                    return map;
                }).collect(Collectors.toList());
                
                return new ResponseEntity<>(eventList, HttpStatus.OK);

            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

     
       
       
private String getCategoryColor(Long crt) {
    if (crt == null) return "#adb5bd"; // 기본 회색

    switch (crt.intValue()) {
        case 1: // 음방 (핑크)
            return "#ff6b81"; 
        case 2: // 자컨 (하늘)
            return "#4dabf7"; 
        case 3: // 방송 (연두)
            return "#51cf66";
        case 4: // 공연 (노랑)
            return "#f4a261"; 
        case 5: // 팬미팅 (주황)
            return "#ff7f50"; 
        case 6: // 곡발매 (보라)
            return "#8e44ad";
        default:
            return "#adb5bd";
    }
}
    
    @PostMapping()
    public ResponseEntity<?> create(@RequestBody Plan plan) {
        try {
            System.out.println("나옴?");
            System.out.println(plan + "ㅇ");
            int result = planServiceImpl.insert(plan);
            System.out.println(result);
            if(result > 0){
                return new ResponseEntity<>("OK", HttpStatus.OK);
            }
                return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping()
    public ResponseEntity<?> update(@RequestBody Plan plan) {
        try {
             int result = planServiceImpl.update(plan);
            if(result > 0){
                return new ResponseEntity<>("OK", HttpStatus.OK);
            }
                return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/{no}")
    public ResponseEntity<?> destroy(@PathVariable Long no) {
        try {
            int result = planServiceImpl.delete(no);
            if(result > 0){
                return new ResponseEntity<>("OK", HttpStatus.OK);
            }
                return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}
