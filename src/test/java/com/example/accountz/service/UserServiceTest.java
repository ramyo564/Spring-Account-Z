package com.example.accountz.service;

import com.example.accountz.exception.GlobalException;
import com.example.accountz.model.UserDto;
import com.example.accountz.persist.entity.UserEntity;
import com.example.accountz.persist.repository.UserRepository;
import com.example.accountz.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("중복 아이디 실패")
    void failedRegisterUser() {
        // given
        UserDto.SignUp signUpDto = new UserDto.SignUp();
        signUpDto.setName("John");
        signUpDto.setEmail("john@example.com");
        signUpDto.setPassword("password");
        signUpDto.setBirthDay(LocalDate.of(
                1990, 5, 15));

        when(userRepository.existsByEmail(
                eq(signUpDto.getEmail()))).thenReturn(true);

        // when
        GlobalException exception = assertThrows(GlobalException.class,
                () -> userService.registerUser(signUpDto));

        // then
        assertEquals(
                ErrorCode.ALREADY_REGISTERED_EMAIL,
                exception.getErrorCode());
    }

    @Test
    void testRegisterUser_WhenEmailAlreadyRegistered_ThrowsException() {
        // Given
        UserDto.SignUp member = new UserDto.SignUp();
        member.setEmail("test@example.com");
        member.setPassword("password");

        // When
        when(userRepository.existsByEmail(
                eq(member.getEmail()))).thenReturn(true);

        // Then
        assertThrows(GlobalException.class, () ->
                userService.registerUser(member));
        verify(userRepository,
                times(1))
                .existsByEmail(eq(member.getEmail()));
    }
    @Test
    @DisplayName("회원가입 성공 1")
    void testRegisterUser_WhenEmailNotRegistered_SavesUser() {
        // Given
        UserDto.SignUp member = new UserDto.SignUp();
        member.setEmail("test@example.com");
        member.setPassword("encodedPassword");

        // When
        when(userRepository.existsByEmail(eq(member.getEmail())))
                .thenReturn(false);
        when(passwordEncoder.encode(eq(member.getPassword())))
                .thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(new UserEntity());

        UserEntity savedUser = userService.registerUser(member);

        // Then
        verify(userRepository, times(1))
                .existsByEmail(eq(member.getEmail()));
        verify(passwordEncoder, times(1))
                .encode(eq("encodedPassword"));
        verify(userRepository, times(1))
                .save(any(UserEntity.class));
        assertNotNull(savedUser);
    }

    @Test
    @DisplayName("회원가입 성공 2")
    void testAuthenticateUser_WhenUserNotFound_ThrowsException() {
        // Given
        UserDto.SignIn member = new UserDto.SignIn();
        member.setEmail("test@example.com");
        member.setPassword("password");

        // When
        when(userRepository.findByEmail(eq(member.getEmail())))
                .thenReturn(Optional.empty());

        // Then
        assertThrows(GlobalException.class, () ->
                userService.authenticateUser(member));
        verify(userRepository, times(1))
                .findByEmail(eq(member.getEmail()));
    }

    @Test
    @DisplayName("패스워드가 틀렸을 경우")
    void testAuthenticateUser_WhenPasswordIsWrong_ThrowsException() {
        // Given
        UserDto.SignIn member = new UserDto.SignIn();
        member.setEmail("test@example.com");
        member.setPassword("wrongPassword");

        UserEntity user = new UserEntity();
        user.setEmail("test@example.com");
        user.setPassword("correctPassword");

        // When
        when(userRepository.findByEmail(eq(member.getEmail())))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(eq(member.getPassword()),
                eq(user.getPassword()))).thenReturn(false);

        // Then
        assertThrows(GlobalException.class, () ->
                userService.authenticateUser(member));
        verify(userRepository, times(1))
                .findByEmail(eq(member.getEmail()));
        verify(passwordEncoder, times(1))
                .matches(eq(member.getPassword()),
                        eq(user.getPassword()));
    }

    @Test
    @DisplayName("로그인 성공")
    void testAuthenticateUser_WhenUserFoundAndPasswordMatches_ReturnsUser() {
        // Given
        UserDto.SignIn member = new UserDto.SignIn();
        member.setEmail("test@example.com");
        member.setPassword("correctPassword");

        UserEntity user = new UserEntity();
        user.setEmail("test@example.com");
        user.setPassword("correctPassword");

        // When
        when(userRepository.findByEmail(eq(member.getEmail())))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(eq(member.getPassword()),
                eq(user.getPassword()))).thenReturn(true);

        UserEntity authenticatedUser =
                userService.authenticateUser(member);

        // Then
        verify(userRepository, times(1))
                .findByEmail(eq(member.getEmail()));
        verify(passwordEncoder, times(1))
                .matches(eq(member.getPassword()),
                        eq(user.getPassword()));
        assertNotNull(authenticatedUser);
        assertEquals(user, authenticatedUser);
    }

    @Test
    @DisplayName("이메일 갖고 오기 성공")
    void testLoadUserByUsername() {
        // Given
        String email = "test@example.com";
        UserEntity user = new UserEntity();
        user.setEmail(email);

        // When
        when(userRepository.findByEmail(eq(email)))
                .thenReturn(Optional.of(user));

        UserDetails loadedUser = userService.loadUserByUsername(email);

        // Then
        verify(userRepository, times(1))
                .findByEmail(eq(email));
        assertNotNull(loadedUser);
        assertEquals(user, loadedUser);
    }

//    @Test
//    void testSaveUser_WhenExceptionOccurs_ThrowsGlobalException() {
//        // Given
//        UserDto.SignUp member = new UserDto.SignUp();
//        member.setEmail("test@example.com");
//        member.setPassword("password");
//
//        // When
//        when(userRepository.save(any(UserEntity.class)))
//                .thenThrow(new RuntimeException("Error saving user"));
//
//        // Then
//        assertThrows(GlobalException.class, () ->
//                userService.saveUser(member));
//        verify(userRepository, times(1))
//                .save(any(UserEntity.class));
//    }
}