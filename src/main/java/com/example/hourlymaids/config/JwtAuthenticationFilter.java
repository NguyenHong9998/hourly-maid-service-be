package com.example.hourlymaids.config;

import com.example.hourlymaids.constant.CustomError;
import com.example.hourlymaids.constant.CustomException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private OrRequestMatcher requestMatcher = new OrRequestMatcher(SecurityConfig.PERMIT_AUTHENTICATION_URLS.stream()
            .map(AntPathRequestMatcher::new).collect(Collectors.toList()));

    @Autowired
    private TokenProvider tokenProvider;

    /**
     * @param object
     * @return String
     * @throws JsonProcessingException
     * @description convert object to Json
     */
    private String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!requestMatcher.matches(request)) {
            String token = tokenProvider.resolveToken(request);
            try {
                if (tokenProvider.validateToken(token)) {
                    Authentication auth = tokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (CustomException ex) {
                SecurityContextHolder.clearContext();
                response.setStatus(ex.getHttpStatus().value());
                response.setContentType("application/json");
                response.getWriter().write(convertObjectToJson(ResponseDataAPI.builder()
                        .success(false)
                        .error(Collections.singletonList(new CustomError(null, ex.getCode(), ex.getMessage())))
                        .build()));
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}