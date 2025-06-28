package com.fifty.fifty.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import com.fifty.fifty.domain.Profile;

@Mapper
public interface ProfileMapper {
    
    public List<Profile> list() throws Exception;

    public int update(Profile profile) throws Exception;

}
