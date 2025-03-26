package katebu_community.community_be.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 파일 업로드 메서드
    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 고유한 파일명 생성
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

        // 확장자 확인
        if (!isImageExtension(extension)) {
            throw new RuntimeException("이미지 파일만 업로드할 수 있습니다. (jpg, png, jpeg, gif, webp)");
        }

        String newFileName = UUID.randomUUID() + "." + extension;

        // 저장할 경로
        File dest = new File(System.getProperty("user.dir") + File.separator + uploadDir + File.separator + newFileName);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        System.out.println("업로드 경로: " + dest.getAbsolutePath());

        try {
            file.transferTo(dest); // 파일 저장
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("파일 업로드 실패", e);
        }

        // 정적 리소스 URL로 반환
        return "/uploads/" + newFileName;
    }

    // 기존 이미지 삭제 메서드
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;

        // "/uploads/파일명" 형태에서 파일명만 추출
        String fileName = imageUrl.replace("/uploads/", "");
        File file = new File(System.getProperty("user.dir") + File.separator + uploadDir + File.separator + fileName);

        if (file.exists()) {
            boolean deleted = file.delete();
            System.out.println("이미지 삭제 성공 여부: " + deleted);
        } else {
            System.out.println("삭제할 이미지가 존재하지 않습니다: " + file.getAbsolutePath());
        }
    }

    // 이미지 확장자 체크
    private boolean isImageExtension(String extension) {
        return extension.matches("jpg|jpeg|png|gif|webp");
    }
}
