package com.fifty.fifty.domain;

import java.nio.file.Path;
import java.util.Date;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class FilePath {
    private Long no;
    private String id;
    private String crt;
    private String path;
    private String name;
    private Long size;
    private String trueDay;
    private Date createdAt; 
    private String oldFilePath;

    private MultipartFile data;

        public FilePath(){
            this.id = UUID.randomUUID().toString();
        }

}
