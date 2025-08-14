package com.fifty.fifty.domain;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class Months {
    private int id;
    private int month;
    private String imageUrl;

    private String oldFilePath;
    private MultipartFile data;
}
