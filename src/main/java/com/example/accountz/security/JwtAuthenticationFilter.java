package com.example.accountz.security;


import com.example.accountz.exception.ErrorResponse;
import com.example.accountz.exception.GlobalException;
import com.example.accountz.type.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  public static final String TOKEN_HEADER = "Authorization";
  public static final String TOKEN_PREFIX = "Bearer ";

  private final TokenProvider tokenProvider;


  private String resolveTokenFromRequest(HttpServletRequest request) {
    String token = request.getHeader(TOKEN_HEADER);

    if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
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

    if (token != null && this.tokenProvider.isBlacklisted(token)) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.setContentType("application/json");
      String errorMessage = "{\"error\": \"Unauthorized\", \"message\": \"Your token is blacklisted.\"}";
      response.getOutputStream().write(errorMessage.getBytes());
      //throw new GlobalException(ErrorCode.WRONG_TOKEN);
      // 이부분 콘솔에만 예외처리됨 -> 나중에 고쳐야함
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