package com.fifty.fifty.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fifty.fifty.domain.FilePath;
import com.fifty.fifty.service.FilesServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/files")
@Slf4j
public class FilesController {
    
    @Autowired
    private FilesServiceImpl filesService;

        @GetMapping
    public ResponseEntity<?> getFiles(
    @RequestParam(name = "crt", required = false) String crt,
    @RequestParam(name = "trueDay", required = false) String trueDay,
    @RequestParam(name = "page", defaultValue = "0") int page,
    @RequestParam(name = "rows", defaultValue = "10") int rows

    ) throws Exception {
        int start = page * rows;
    
        Map<String, Object> params = new HashMap<>();
        params.put("crt", crt);
        params.put("trueDay", trueDay);
        params.put("start", start);
        params.put("rows", rows);

        List<FilePath> list = filesService.list(params);
        int total = filesService.count(params);

        Map<String, Object> result = new HashMap<>();

        result.put("list", list);
        result.put("total", total);

        return ResponseEntity.ok(result);
        
    }
    

    @PostMapping("")
    public ResponseEntity<?> create(@ModelAttribute FilePath file) {
        try {
            int result = filesService.insert(file);
    
            if(result > 0){
                return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/update")
    public ResponseEntity<?> update(@ModelAttribute FilePath file) {
          try {

            int result = filesService.update(file);
            if(result > 0){
                return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/{no}")
    public ResponseEntity<?> destroy(@PathVariable Long no) {
          try {
            int result = filesService.delete(no);
            if(result > 0){
                return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    

}
