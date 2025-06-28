package com.fifty.fifty.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fifty.fifty.domain.Notice;
import com.fifty.fifty.mapper.NoticeMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    public NoticeMapper noticeMapper;

    @Override
    public List<Notice> list() throws Exception {
        List<Notice> noticeList = noticeMapper.list();
        return noticeList;
    }

    @Override
    public Notice selecetByNo(Long no) throws Exception {
        Notice notice = noticeMapper.selecetByNo(no);
        return notice;
    }

    @Override
    public int insert(Notice notice) throws Exception {
        int result = noticeMapper.insert(notice);
        return result;
    }

    @Override
    public int update(Notice notice) throws Exception {
       int result = noticeMapper.update(notice);
       return result;
    }

    @Override
    public int delete(Long no) throws Exception {
        int result = noticeMapper.delete(no);
        return result;
    }
    
}
