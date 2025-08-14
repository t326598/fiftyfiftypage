package com.fifty.fifty.service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fifty.fifty.domain.FilePath;
import com.fifty.fifty.domain.Files;
import com.fifty.fifty.mapper.FilesMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class FilesServiceImpl implements FilesService {

   

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    public FilesMapper filesMapper;



   @Override
    public int insert(FilePath file) throws Exception {
        int result = 0;
        MultipartFile multipartFile = file.getData();
        
        if(multipartFile.isEmpty()){
            return result;
        }

    String originName = multipartFile.getOriginalFilename();
    long fileSize = multipartFile.getSize();
    String fileName = UUID.randomUUID().toString() + "_" + originName;
    byte[] fileData = multipartFile.getBytes();
    String filePath = uploadPath + "/" + fileName;

    File uploadFile = new File(filePath);

    File parentDir = uploadFile.getParentFile();
    if (!parentDir.exists()) {
        parentDir.mkdirs();  // 디렉토리 생성
}

    FileCopyUtils.copy(fileData, uploadFile);

    file.setName(fileName);
    file.setPath(filePath);
    file.setSize(fileSize);
    try {
    result = filesMapper.insert(file);
    } catch (Exception e) {
        e.printStackTrace(); // 반드시 직접 호출
        throw e;
    }
    return result;

}

    @Override
    public int update(FilePath files) throws Exception {
        int result = filesMapper.update(files);

        return result;
    }

    @Override
    public int delete(Long no) throws Exception {
       int reustl = filesMapper.delete(no);

       return reustl;
    }

    @Override
    public int insertList(List<FilePath> fileList) throws Exception {
        int result = 0;
        if(fileList == null || fileList.isEmpty())
        return result;

        for (FilePath files : fileList){
            result += insert(files);
        }
        return result;
    }

    @Override
    public List<FilePath> list(Map<String, Object> params) throws Exception {
        return filesMapper.list(params); 
    }

    @Override
    public int count(Map<String, Object> params) throws Exception {
        return filesMapper.count(params); 
    }

 
    @Override
    public int allCount(Map<String, Object> params) throws Exception {
        return filesMapper.allCount(params);
    }


    @Override
    public List<FilePath> AllList() throws Exception {
        return filesMapper.AllList();
    }

    @Override
    public int mainCount(Map<String, Object> params) throws Exception {
        return filesMapper.mainCount(params); 
    }


     @Override
    public FilePath select(Long no) throws Exception {
        return filesMapper.select(no);
    }
    
    
}
