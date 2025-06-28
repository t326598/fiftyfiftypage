package com.fifty.fifty.controller;

import java.net.http.HttpRequest;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SystemProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fifty.fifty.domain.Visit;
import com.fifty.fifty.domain.VisitSummary;
import com.fifty.fifty.service.VisitService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/visit")
public class VisitController {
    
    @Autowired
    private VisitService visitService;


    @PostMapping("")
    public void checkVisit(HttpServletRequest request, HttpServletResponse response) {
        boolean isNewVisitor = true;

        if(request.getCookies() != null){
            for(var cookie : request.getCookies()){
                if("is_visited_today".equals(cookie.getName())){
                    isNewVisitor = false;
                    break;
                }
            }
        }

        if(isNewVisitor){
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();
            long secondsUntilMidnight = Duration.between(now, midnight).getSeconds();

            Cookie visitCookie = new Cookie("is_visited_today", "true");
            visitCookie.setMaxAge((int) secondsUntilMidnight);
            visitCookie.setPath("/");
            response.addCookie(visitCookie);
        }

        visitService.saveVisit(isNewVisitor);


        }
            // 일 방문자 수 조회
            @GetMapping("/today")
            public ResponseEntity<?> getTodayStats() {
                VisitSummary today =  visitService.getTodayStats();
                return new ResponseEntity<>(today, HttpStatus.OK);
            }

            
            @GetMapping("/summary")
            public ResponseEntity<?> getTotalStats() {
                VisitSummary summary = visitService.getTotalStats();

                return new ResponseEntity<>(summary, HttpStatus.OK);
            }
 
        
            @GetMapping("/stats")
            public ResponseEntity<?> getStats(@RequestParam("start") String start, @RequestParam("end") String end) {
                LocalDate startDate = LocalDate.parse(start, DateTimeFormatter.ISO_DATE);
                LocalDate endDate = LocalDate.parse(end, DateTimeFormatter.ISO_DATE);
                List<Visit> visitList = visitService.getStatsByDate(startDate, endDate);
                return new ResponseEntity<>(visitList, HttpStatus.OK);
            }




    }
    


