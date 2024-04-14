package com.example.accountz.webController;

import com.example.accountz.model.UserDto;
import com.example.accountz.security.TokenProvider;
import com.example.accountz.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final TokenProvider tokenProvider;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(
            @RequestBody @Valid UserDto.SignUp request) {

        this.userService.registerUser(request);

        return ResponseEntity.ok().body(
                new ApiResponse("회원가입","Success"));

    }

    @PostMapping("/log-in")
    public ResponseEntity<?> signIn(
            @RequestBody UserDto.SignIn request) {

        var member =
                this.userService.authenticateUser(request);

        var token =
                this.tokenProvider.generateToken(
                        member.getEmail()
                );


        return ResponseEntity.ok(new ApiResponse("Token",token));

    }

    @PostMapping("/log-out")
    public ResponseEntity<?> logout(
            @RequestHeader("Authorization") String token) {
        // Authorization 헤더에서 토큰 추출
        String jwt = token.substring(7); // "Bearer " 제거

        // 토큰을 블랙리스트에 등록
        this.tokenProvider.addToBlacklist(jwt);

        // 로그아웃 성공 응답 반환
        return ResponseEntity.ok().build();

    }
    @GetMapping("/blacklist")
    public ResponseEntity<Set<String>> getBlacklist() {
        return ResponseEntity.ok(tokenProvider.getBlacklist());
    }
}
