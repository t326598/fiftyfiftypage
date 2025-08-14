package com.fifty.fifty.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fifty.fifty.domain.Profile;
import com.fifty.fifty.service.ProfileServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
// ↑ 위 줄은 실제로 존재하지 않는 별칭입니다. 아래에 올바른 import를 사용합니다.

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/profile")
@Slf4j
@Tag(name = "Profile", description = "프로필 조회/수정 및 파일 업로드 API")
public class ProfileController {

    @Autowired
    private ProfileServiceImpl profileServiceImpl;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping
    @Operation(
        summary = "프로필 목록 조회",
        description = "등록된 모든 프로필을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity getMethodName() throws Exception {
        List<Profile> list = profileServiceImpl.list();

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);

        return ResponseEntity.ok(result);
    }

    @PutMapping()
    @Operation(
        summary = "프로필 수정",
        description = "요청 본문의 Profile 정보를 기반으로 프로필을 수정합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "요청 본문 오류"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> updateProfile(
        @Parameter(description = "수정할 프로필 정보", required = true,
            content = @Content(schema = @Schema(implementation = Profile.class)))
        @RequestBody Profile request
    ) {
        try {
            if (request.getOldFilePath() != null && !request.getOldFilePath().equals(request.getFilePath())) {
                Path oldFile = Paths.get("/path/to/upload/dir", request.getOldFilePath());
                Files.deleteIfExists(oldFile);
            }
            int result = profileServiceImpl.update(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("프로필 수정 실패");
        }
    }

@PostMapping
@Operation(
    summary = "파일 업로드",
    description = "멀티파트 파일을 업로드하고 저장된 파일명을 반환합니다."
)
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "업로드 성공"),
    @ApiResponse(responseCode = "400", description = "요청 오류"),
    @ApiResponse(responseCode = "500", description = "서버 오류")
})
public ResponseEntity<Map<String, String>> uploadFile(
    @Parameter(
        description = "업로드할 파일",
        required = true,
        content = @Content(
            mediaType = "multipart/form-data",
            schema = @Schema(type = "string", format = "binary")
        )
    )
    @RequestParam(value = "file", required = true) MultipartFile file
) throws IOException {
    System.out.println("나옴:?" + file);

    String originName = file.getOriginalFilename();
    long fileSize = file.getSize();
    String fileName = UUID.randomUUID().toString() + "_" + originName;
    byte[] fileData = file.getBytes();
    String filePath = uploadPath + "/" + fileName;

    File uploadFile = new File(filePath);
    File parentDir = uploadFile.getParentFile();
    if (!parentDir.exists()) {
        parentDir.mkdirs();
    }
    FileCopyUtils.copy(fileData, uploadFile);

    Map<String, String> response = new HashMap<>();
    response.put("filePath", fileName);
    return ResponseEntity.ok(response);
}
}
