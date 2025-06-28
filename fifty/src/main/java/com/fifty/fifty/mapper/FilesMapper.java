package com.fifty.fifty.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.multipart.MultipartFile;

import com.fifty.fifty.domain.FilePath;

 @Mapper
public interface FilesMapper {
    
    public List<FilePath> AllList() throws Exception;

    public int allCount(Map<String, Object> params) throws Exception;

    public List<FilePath> list(Map<String, Object> params) throws Exception;

    public int count(Map<String, Object> params);

    public int insert(FilePath files) throws Exception;

    public int update(FilePath files) throws Exception;

    public int delete(Long no) throws Exception;

}
