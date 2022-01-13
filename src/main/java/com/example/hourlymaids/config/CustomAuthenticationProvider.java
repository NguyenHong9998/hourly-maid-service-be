package com.example.hourlymaids.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.example.hourlymaids.constant.CustomException;
import com.example.hourlymaids.constant.Error;
import com.example.hourlymaids.domain.LoginUser;
import com.example.hourlymaids.domain.RoleDomain;
import com.example.hourlymaids.domain.UserDomain;
import com.example.hourlymaids.entity.*;
import com.example.hourlymaids.repository.AccountRepository;
import com.example.hourlymaids.repository.RoleRepository;
import com.example.hourlymaids.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


/**
 * The type Custom authentication provider.
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private BCryptPasswordEncoder bCryptEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            LoginUser username = authentication.getPrincipal() != null ? (LoginUser) authentication.getPrincipal() : null;
            String password = authentication.getCredentials().toString();
            UserDomain userDTO = new UserDomain();
            AccountEntity accountEntity = accountRepository.findByEmail(username.getEmail());
            if (accountEntity != null) {
                UserEntity userEntity = userRepository.findByAccountId(accountEntity.getId());
                userDTO.setUserId(userEntity.getId());
                userDTO.setEmail(accountEntity.getEmail());
                RoleEntity roleEntity = roleRepository.findById(accountEntity.getRoleId()).orElse(null);
                userDTO.setRoles(Arrays.asList(new RoleDomain(roleEntity.getId(), roleEntity.getName())));
                userDTO.setPassword(accountEntity.getPassword());
                // Check login id and password
                if (bCryptEncoder.matches(password, userDTO.getPassword())) {
                    Set<SimpleGrantedAuthority> list_authorities = new HashSet<>();

                    list_authorities.add(new SimpleGrantedAuthority("ROLE" + roleEntity.getName()));

                    return new UsernamePasswordAuthenticationToken(userDTO, password, list_authorities);
                }
                throw new CustomException(Error.INVALID_EMAIL_OR_PASSWORD.getMessage(),
                        Error.INVALID_EMAIL_OR_PASSWORD.getCode(),
                        HttpStatus.BAD_REQUEST);

            } else {
                throw new CustomException(Error.INVALID_EMAIL_OR_PASSWORD.getMessage(),
                        Error.INVALID_EMAIL_OR_PASSWORD.getCode(),
                        HttpStatus.BAD_REQUEST);
            }
        } else {
            throw new CustomException(Error.EXTERNAL_LOGIN_FAIL.getMessage(),
                    Error.EXTERNAL_LOGIN_FAIL.getCode(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }
}
