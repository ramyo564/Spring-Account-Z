package com.example.accountz.webController;

import com.example.accountz.model.UserDto;
import com.example.accountz.persist.entity.UserEntity;
import com.example.accountz.persist.repository.UserRepository;
import com.example.accountz.security.TokenProvider;
import com.example.accountz.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
class UserControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private TokenProvider tokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @WithMockUser
    @Test
    @DisplayName("회원가입 성공")
    void signUp() throws Exception {
        //given
        given(userRepository.existsByEmail(anyString()))
                .willReturn(false);
        given(passwordEncoder.encode(anyString()))
                .willReturn("encodedPassword");

        //when
        UserDto.SignUp signUpDto = new UserDto.SignUp();
        signUpDto.setName("John");
        signUpDto.setEmail("john@example.com");
        signUpDto.setPassword("password");
        signUpDto.setBirthDay(
                LocalDate.of(1990, 5, 15));

        
        // 결과 확인
        ResultActions resultActions =
                mockMvc.perform(
                        post("/auth/sign-up")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        signUpDto)
                                )
                        )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title")
                    .value("회원가입"))
                .andExpect(jsonPath("$.detail")
                    .value("Success"));
    }

    @WithMockUser
    @Test
    @DisplayName("로그인 성공")
    void signIn()throws Exception {
        // Given

        UserDto.SignIn signInRequest = new UserDto.SignIn();
        signInRequest.setEmail("123@123.com");
        signInRequest.setPassword("12345678");
        String token = "generated_token";
        given(userService.authenticateUser(signInRequest))
                .willReturn(
                        UserEntity.builder()
                                .id(12L)
                                .name("qwe")
                                .email("123@123.com")
                                .birthDay(LocalDate.of(
                                        1999,12,12))
                                .password("12345678")
                                .build());
        given(tokenProvider.generateToken("123@123.com"))
                .willReturn(token);
        //when
        //then
        mockMvc.perform(post("/auth/sign-in")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                signInRequest)))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title")
                        .value("Token"))
                .andExpect(jsonPath("$.detail")
                .value("generated_token"));

    }
    @WithMockUser
    @Test
    @DisplayName("로그아웃 성공")
    void logoutSuccess() throws Exception {
        // given
        given(tokenProvider.isBlacklisted(anyString())).willReturn(false);

        // when
        String token = "Bearer abc123";

        // then
        mockMvc.perform(
                        post("/auth/log-out")
                                .with(csrf())
                                .header("Authorization", token)
                )
                .andExpect(status().isOk());

        then(tokenProvider).should().addToBlacklist("abc123");
    }
}