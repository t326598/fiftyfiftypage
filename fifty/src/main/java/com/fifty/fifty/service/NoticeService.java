package com.fifty.fifty.service;

import java.util.List;


import com.fifty.fifty.domain.Notice;

public interface NoticeService {
    
    public List<Notice> list() throws Exception;

    public Notice selecetByNo(Long no) throws Exception;

    public int insert(Notice notice) throws Exception;

    public int update(Notice notice) throws Exception;

    public int delete(Long no) throws Exception;


}
