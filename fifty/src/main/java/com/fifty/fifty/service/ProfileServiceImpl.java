package com.fifty.fifty.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fifty.fifty.domain.Profile;
import com.fifty.fifty.mapper.ProfileMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private ProfileMapper profileMapper;

    @Override
    public List<Profile> list() throws Exception {
        List<Profile> profileList = profileMapper.list();
        return profileList;
    }

    @Override
    public int update(Profile profile) throws Exception {
        int result = profileMapper.update(profile);
        return result;
    }
    
}
