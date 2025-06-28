package com.fifty.fifty.service;

import java.util.List;
import java.util.Map;

import com.fifty.fifty.domain.FilePath;


public interface FilesService {
    
    public List<FilePath> AllList() throws Exception;

    public int allCount(Map<String, Object> params) throws Exception;

    public List<FilePath> list(Map<String, Object> params) throws Exception;

    public int count(Map<String, Object> params) throws Exception;

    public int insert(FilePath files) throws Exception;

    public int insertList(List<FilePath> fileList) throws Exception;

    public int update(FilePath files) throws Exception;

    public int delete(Long no) throws Exception;

}
