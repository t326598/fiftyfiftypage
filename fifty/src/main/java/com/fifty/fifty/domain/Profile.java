package com.fifty.fifty.domain;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class Profile {
    private Long no;
    private Long fileNo;
    private String content;
    private String title;
    private String subContent;
    private String name;

    private String path;

    private MultipartFile data;
}
