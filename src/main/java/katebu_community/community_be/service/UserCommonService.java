package katebu_community.community_be.service;

import katebu_community.community_be.domain.User;
import katebu_community.community_be.exception.EmailNotFoundException;
import katebu_community.community_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCommonService {

    private final UserRepository userRepository;

    // userId로 유저 조회
    public User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EmailNotFoundException("회원 조회 실패"));
    }
}
