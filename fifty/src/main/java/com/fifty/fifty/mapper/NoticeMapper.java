package com.fifty.fifty.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import com.fifty.fifty.domain.Notice;

@Mapper
public interface NoticeMapper {

    public List<Notice> list() throws Exception;

    public Notice selecetByNo(Long no) throws Exception;

    public int insert(Notice notice) throws Exception;

    public int update(Notice notice) throws Exception;

    public int delete(Long no) throws Exception;

}
