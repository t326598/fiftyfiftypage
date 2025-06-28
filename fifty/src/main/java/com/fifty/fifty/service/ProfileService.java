package com.fifty.fifty.service;

import java.util.List;

import com.fifty.fifty.domain.Profile;

public interface ProfileService {
    
      public List<Profile> list() throws Exception;

    public int update(Profile profile) throws Exception;
}
