package com.example.accountz.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider;


    private String resolveTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);

        if (!StringUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String token = this.resolveTokenFromRequest(request);

        // 토큰이 블랙리스트에 등록되었는지 확인
        if (token != null && this.tokenProvider.isBlacklisted(token)) {
            // 블랙리스트에 등록된 토큰이면 인증 실패 처리
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        if (StringUtils.hasText(token)
                && this.tokenProvider.validateToken(token)) {
            Authentication auth =
                    this.tokenProvider.getAuthentication(token);

            SecurityContextHolder.getContext().setAuthentication(auth);
            log.info(String.format(
                    "[%s] -> %s",
                    this.tokenProvider.getUsername(token),
                    request.getRequestURI()));
        }
        filterChain.doFilter(request, response);
    }
}
