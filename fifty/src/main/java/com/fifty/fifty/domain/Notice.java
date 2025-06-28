package com.fifty.fifty.domain;

import java.util.Date;

import lombok.Data;

@Data
public class Notice {
    private Long no;
    private Long fileNo;
    private String title;
    private String content;
    private Date createdAt;
    private Date updatedAt;
}
