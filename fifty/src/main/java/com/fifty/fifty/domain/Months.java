package com.fifty.fifty.domain;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class Months {
    private int no;
    private int fileNo;
    private int month;
    private String name;

    private String oldFilePath;
    private MultipartFile data;
}
