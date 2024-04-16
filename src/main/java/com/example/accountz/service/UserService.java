package com.example.accountz.service;

import com.example.accountz.exception.GlobalException;
import com.example.accountz.model.UserDto;
import com.example.accountz.persist.entity.UserEntity;
import com.example.accountz.persist.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.accountz.type.ErrorCode.*;

@Slf4j
@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity registerUser(UserDto.SignUp member)
            throws GlobalException {
        boolean exists =
                this.userRepository.existsByEmail(
                        member.getEmail());

        if (exists) {
            throw new GlobalException(ALREADY_REGISTERED_EMAIL);
        }

        member.setPassword(
                this.passwordEncoder.encode(member.getPassword()));

        return saveUser(member);


    }

    private UserEntity saveUser(UserDto.SignUp member) {

        try {
            return this.userRepository.save(member.toEntity());

        }catch (Exception e){
            throw new GlobalException(INTERNAL_SERVER_ERROR, e.getMessage());
        }


    }

    public UserEntity authenticateUser(UserDto.SignIn member)
            throws GlobalException {
        var user =
                this.userRepository.findByEmail(
                                member.getEmail())
                        .orElseThrow(() ->
                                new GlobalException(USER_NOT_FOUND));

        if (!this.passwordEncoder.matches(
                member.getPassword(),
                user.getPassword())
        ) {
            throw new GlobalException(WRONG_PASSWORD);
        }

        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(USER_NOT_FOUND));
    }


}
