package com.example.accountz.webController;

import com.example.accountz.model.UserDto;
import com.example.accountz.security.TokenProvider;
import com.example.accountz.service.UserService;
import jakarta.validation.Valid;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        new ApiResponse("회원가입", "Success"));

  }

  @PostMapping("/log-in")
  public ResponseEntity<?> signIn(
      @RequestBody UserDto.SignIn request) {

    var member = this.userService.authenticateUser(request);
    var token = this.tokenProvider.generateToken(member.getEmail());

    return ResponseEntity.ok(new ApiResponse("Token", token));

  }

  @PostMapping("/log-out")
  public ResponseEntity<?> logout(
      @RequestHeader("Authorization") String token) {

    String jwt = token.substring(7);
    this.tokenProvider.addToBlacklist(jwt);

    return ResponseEntity.ok().build();

  }

  @PreAuthorize("hasRole('ROLE_OWNER')")
  @GetMapping("/blacklist")
  public ResponseEntity<Set<String>> getBlacklist() {
    return ResponseEntity.ok(tokenProvider.getBlacklist());
  }
}
