package katebu_community.community_be.service;

import katebu_community.community_be.domain.User;
import katebu_community.community_be.dto.UserDto;
import katebu_community.community_be.exception.DuplicateException;
import katebu_community.community_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileUploadService fileUploadService;
    private final UserCommonService userCommonService;

    // 회원정보 조회
    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(user -> UserDto.builder()
                        .userId(user.getId())
                        .nickname(user.getNickname())
                        .email(user.getEmail())
                        .profileImg(user.getProfileUrl())
                        .build())
                .orElse(null);
    }

    // 회원정보 수정
    @Transactional
    public UserDto updateUser(Long userId, String nickname, MultipartFile profileImg) {
        // 회원 조회
        User user = userCommonService.getUserOrThrow(userId);

        // 닉네임이 변경되었으면 중복 체크
        if (!user.getNickname().equals(nickname) && userRepository.existsByNickname(nickname)) {
            throw new DuplicateException("중복된 닉네임입니다.");
        }

        // 닉네임 업데이트
        user.setNickname(nickname);

        // 프로필 이미지 업데이트 (파일이 제공된 경우)
        if (profileImg != null && !profileImg.isEmpty()) {
            String profileImgUrl = fileUploadService.uploadImage(profileImg);
            user.setProfileUrl(profileImgUrl);
        }

        // 수정된 회원 저장 및 DTO 변환
        userRepository.save(user);
        return UserDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profileImg(user.getProfileUrl())
                .build();
    }

    // 비밀번호 변경
    @Transactional
    public void changePassword(Long userId, String newPassword) {
        // 회원 조회
        User user = userCommonService.getUserOrThrow(userId);

        // 비밀번호 수정
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // 회원 탈퇴
    @Transactional
    public void deleteUser(Long userId) {
        // 회원 조회
        User user = userCommonService.getUserOrThrow(userId);

        // 회원 삭제
        userRepository.delete(user);
    }
}
