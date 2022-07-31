package com.ssafy.drinkus.user.service;

import com.ssafy.drinkus.common.*;
import com.ssafy.drinkus.common.type.TokenType;
import com.ssafy.drinkus.auth.Auth;
import com.ssafy.drinkus.auth.AuthRepository;
import com.ssafy.drinkus.email.request.UserNameAuthRequest;
import com.ssafy.drinkus.email.request.UserNameCheckRequest;
import com.ssafy.drinkus.email.service.EmailService;
import com.ssafy.drinkus.emailauth.EmailAuth;
import com.ssafy.drinkus.auth.response.TokenResponse;
import com.ssafy.drinkus.security.util.JwtUtil;
import com.ssafy.drinkus.user.domain.User;
import com.ssafy.drinkus.user.domain.UserRepository;
import com.ssafy.drinkus.user.request.*;
import com.ssafy.drinkus.user.response.UserMyInfoResponse;
import com.ssafy.drinkus.user.response.UserProfileResponse;
import com.ssafy.drinkus.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    final int POPULARITY_LIMIT = 5;

    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    @Transactional
    public void createUser(UserCreateRequest request) {
        if (userRepository.existsByUserName(request.getUserName())) {
            throw new DuplicateException("이미 가입된 회원입니다.");
        }
        User user = User.createUser(request.getUserName(), passwordEncoder.encode(request.getUserPw()), request.getUserFullname(), request.getUserBirthday(), request.getUserName());
        userRepository.save(user);
    }

    @Transactional
    public TokenResponse loginUser(UserLoginRequest request) {
        User findUser = userRepository.findByUserName(request.getUserName())
                .orElseThrow(() -> new NotFoundException(NotFoundException.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getUserPw(), findUser.getUserPw())) {
            throw new NotMatchException(NotMatchException.PW_NOT_MATCH);
        }
        // 이전에 존재하던 RefreshToken들 모두 삭제
        authRepository.deleteByUserId(findUser.getUserId());

        // AccessToken, RefreshToken 발급
        String accesstoken = jwtUtil.createToken(findUser.getUserId(), TokenType.ACCESS_TOKEN);
        String refreshToken = jwtUtil.createToken(findUser.getUserId(), TokenType.REFRESH_TOKEN);

        // RefreshToken 저장
        Auth auth = Auth.builder()
                .userId(findUser.getUserId())
                .refreshToken(refreshToken)
                .build();
        authRepository.save(auth);
        return new TokenResponse(accesstoken, refreshToken);
    }

    //회원수정
    @Transactional
    public void updateUser(Long userId, UserUpdateRequest request) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(NotFoundException.USER_NOT_FOUND));

        findUser.updateUser(request.getUserNickname(), request.getUserIntroduce(), request.getUserSoju(), request.getUserBeer(), request.getUserImg());
    }

    //비밀번호 수정
    @Transactional
    public void updatePassword(Long userId, UserUpdatePasswordRequest request) {
        //회원번호로 회원 조회
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(NotFoundException.USER_NOT_FOUND));

        //이전 비밀번호 같은 지 확인
        if (!passwordEncoder.matches(request.getUserBeforePw(), findUser.getUserPw())) {
            // 예외 던짐 -> 캐치하는곳 필요
            throw new NotMatchException("회원의 비밀번호가 일치하지 않습니다.");
        }

        //새 비밀번호 == 새 비밀번호 확인
        if (!request.getUserPw().equals(request.getUserCheckPw())) {
            throw new NotMatchException("회원의 비밀번호가 일치하지 않습니다.");
        }

        findUser.updateUserPassword(passwordEncoder.encode(request.getUserPw()));
    }

    //아이디 찾기
    public void findByUserName(UserDuplicateCheckIdRequest request) {
        if (userRepository.existsByUserName(request.getUserName())) {
            throw new DuplicateException("이미 가입된 회원입니다.");
        }
    }

    // 닉네임 중복 검사
    public void findByUserNickname(String userNickname) {
        if (userRepository.existsByUserNickname(userNickname)) {
            throw new DuplicateException("이미 존재하는 닉네임입니다.");
        }
    }

    //인기도 수정
    @Transactional
    public void updatePopularity(Long userId, Integer popularNum) {
        // 회원번호 회원을 조회 -> 인기도를 get한다.
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(NotFoundException.USER_NOT_FOUND));

        // 인기도의 수정 값만 바꿔준다
        findUser.updatePopularity(popularNum);
    }

    // 회원 프로필 조회
    public UserProfileResponse findUserProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(NotFoundException.USER_NOT_FOUND));
        return UserProfileResponse.from(user);
    }

    // 회원 내정보 조회
    public UserMyInfoResponse findUserMyInfo(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(NotFoundException.USER_NOT_FOUND));
        return UserMyInfoResponse.from(user);
    }

    // 회원 삭제
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // 아이디 찾기
    public List<String> findMyUserName(UserFindMyIdRequest request) {
        List<User> findUsers = userRepository.findByUserFullnameAndUserBirthday(request.getUserFullname(), request.getUserBirthday());

        return findUsers.stream()
                .map(user -> StringUtil.masking(user.getUserName()))
                .collect(Collectors.toList());
    }

    // 비밀번호 재설정 및 이메일 발송
    @Transactional
    public void resetPw(UserFindMyPwRequest request) {
        User findUser = userRepository.findByUserName(request.getUserName())
                .orElseThrow(() -> new NotFoundException(NotFoundException.USER_NOT_FOUND));

        String password = StringUtil.makeNewPassword();
        findUser.updateUserPassword(passwordEncoder.encode(password));

        // 이메일 발송
        try {
            emailService.sendResetPwEmail(request.getUserName(), password);
        } catch (MessagingException e) {
            throw new MailSendFailException(MailSendFailException.MAIL_SEND_FAIL);
        }
    }

    // 회원가입 이메일 인증 토큰 생성 및 발송
    @Transactional
    public void sendEmailAuthEmail(UserNameCheckRequest request) {
        if (userRepository.existsByUserName(request.getUserName())) {
            throw new DuplicateException("이미 가입된 회원입니다.");
        }

        EmailAuth emailAuth = EmailAuth.createEmailAuth(request.getUserName(), UUID.randomUUID().toString());
        emailService.saveEmailAuth(emailAuth);
        try {
            emailService.sendUserNameCheckEmail(emailAuth.getUserName(), emailAuth.getAuthToken());
        } catch (MessagingException e) {
            throw new MailSendFailException(MailSendFailException.MAIL_SEND_FAIL);
        }
    }

    // 회원가입 이메일 인증 토큰 확인
    @Transactional
    public void confirmUserName(UserNameAuthRequest request) {
        emailService.confirmEmailAuth(request);
    }


    // 인기도 제한 초기화 스케줄 task
    @Scheduled(cron = "0 0 6 * * *") // 매일 6시 정각
    @Transactional
    public void resetPopularityLimit() {
        userRepository.resetUserPopularityLimit(POPULARITY_LIMIT);
    }
}
