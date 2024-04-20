package com.example.accountz.model;

import com.example.accountz.persist.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

public class UserDto {

  @Data
  public static class SignIn {

    private String email;
    private String password;
  }

  @Data
  public static class SignUp {

    @NotBlank
    @Size(min = 2, message = "이름은 두 글자 이상 입력해주세요")
    private String name;

    @NotNull
    @NotBlank
    @Email(message = "올바른 이메일 주소를 입력해주세요")
    private String email;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "MM/dd/yyyy")
    @Past(message = "미래의 날짜는 불가능 합니다.")
    private LocalDate birthDay;

    @NotBlank
    @Size(min = 8, message = "비밀번호는 8자리 이상 입력해주세요")
    private String password;

    private List<String> roles;

    public UserEntity toEntity() {
      return UserEntity.builder()
          .name(this.name)
          .email(this.email)
          .birthDay(this.birthDay)
          .password(this.password)
          .roles(this.roles)
          .build();
    }
  }

}
