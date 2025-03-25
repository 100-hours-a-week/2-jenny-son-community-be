package katebu_community.community_be.service;

import katebu_community.community_be.domain.User;
import katebu_community.community_be.dto.LoginRequestDto;
import katebu_community.community_be.dto.LoginResponseDto;
import katebu_community.community_be.dto.UserDto;
import katebu_community.community_be.exception.DuplicateException;
import katebu_community.community_be.exception.EmailNotFoundException;
import katebu_community.community_be.exception.InvalidInputException;
import katebu_community.community_be.exception.InvalidPasswordException;
import katebu_community.community_be.repository.UserRepository;
import katebu_community.community_be.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final FileUploadService fileUploadService;

    // 회원가입
    @Transactional
    public void register(MultipartFile profileImg,
                         String email,
                         String password,
                         String nickname) {

        // 닉네임 길이 체크
        if (nickname.length() > 10) {
            throw new InvalidInputException("닉네임은 최대 10자까지 작성 가능합니다.");
        }

        // 이메일/닉네임 중복 체크
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateException("중복된 이메일입니다.");
        }
        if (userRepository.existsByNickname(nickname)) {
            throw new DuplicateException("중복된 닉네임입니다.");
        }

        // 이미지 처리 (퍼블릭 URL)
        String profileImgUrl = fileUploadService.uploadImage(profileImg);

        // 비밀번호 암호화 후 저장
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .profileUrl(profileImgUrl)
                .build();

        userRepository.save(user);  // 디비에 저장
    }

    // 로그인
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        // 이메일 확인
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new EmailNotFoundException("등록되지 않은 이메일입니다."));

        // 비밀번호 확인
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 올바르지 않습니다.");
        }

        // 토큰 생성
        String token = jwtTokenProvider.createToken(user.getId());

        // 유저 객체 생성
        UserDto userDto = UserDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profileImg(user.getProfileUrl())
                .build();

        // 유저 객체와 토큰 반환
        return LoginResponseDto.builder()
                .user(userDto)
                .token(token)
                .build();
    }
}
