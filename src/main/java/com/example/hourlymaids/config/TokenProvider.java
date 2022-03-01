package com.example.hourlymaids.config;

import java.io.Serializable;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;

import com.example.hourlymaids.constant.CustomException;
import com.example.hourlymaids.constant.Error;
import com.example.hourlymaids.domain.RoleDomain;
import com.example.hourlymaids.domain.UserDomain;
import com.example.hourlymaids.repository.RoleRepository;
import com.example.hourlymaids.util.AsymmetricCryptography;
import com.example.hourlymaids.util.GenerateKeys;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.example.hourlymaids.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import com.example.hourlymaids.entity.*;

/**
 * The type Token provider.
 */
@Component
public class TokenProvider implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /**
     * The constant SECURITY_KEY_ROOT_DIR.
     */
    public static String SECURITY_KEY_ROOT_DIR = "localKeys";
    private static String SECURITY_KEY_PRIVATE_KEY = "/privateKey";
    private static String SECURITY_KEY_PUBLIC_KEY = "/publicKey";

    private static final String ENCRYPTED_PASSWORD_KEY = "ENCRYPTED_PASSWORD";

    private PublicKey publicKey;
    private PrivateKey privateKey;

    private String ACCESS_TOKEN_VALIDITY_MILISECONDS = "2592000000";
    private String REFRESH_TOKEN_VALIDITY_MILISECONDS = "2592000000";
    private String HEADER_STRING = "Authorization";
    private String TOKEN_PREFIX = "Bearer";
    private String AUTHORITIES_KEY = "scope";
    private String FORGOT_PASSWORD_TOKEN_VALIDITY_MILISECONDS = "1800000";

    @Autowired
    private UserRepository usersRepository;
    @Autowired
    private AsymmetricCryptography cryptography;
    @Autowired
    private RoleRepository roleRepository;

    @PostConstruct
    public void initKeys() throws Exception {
        if (!Files.exists(Paths.get(SECURITY_KEY_PUBLIC_KEY)) || !Files.exists(Paths.get(SECURITY_KEY_PRIVATE_KEY))) {
            GenerateKeys.generatePrivatePublicKey(SECURITY_KEY_ROOT_DIR);
        }

        publicKey = cryptography.getPublic(SECURITY_KEY_PUBLIC_KEY);
        System.out.println("publicKey: " + publicKey);
        privateKey = cryptography.getPrivate(SECURITY_KEY_PRIVATE_KEY);
        System.out.println("privateKey: " + privateKey);

    }

    public String generateToken(Authentication authentication) throws Exception {
        final String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        if ("ROLE_SYS".equals(authorities)) {
            return this.generateAccessTokenNotExpiration(authorities);
        }
        UserDomain userDTO = (UserDomain) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(userDTO.getEmail())
                .claim(AUTHORITIES_KEY, authorities)
                .setId(userDTO.getUserId().toString())
                //.signWith(SignatureAlgorithm.HS256, SIGNING_KEY)
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Long.valueOf(ACCESS_TOKEN_VALIDITY_MILISECONDS)))
                .compact();
    }

    /**
     * Generate refresh token string.
     *
     * @param authentication the authentication
     * @return String string
     * @throws Exception the exception
     * @description generate refresh token
     */
    public String generateRefreshToken(Authentication authentication) throws Exception {
        UserDomain userDTO = (UserDomain) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(userDTO.getEmail())
                .claim(AUTHORITIES_KEY, "REFRESH_TOKEN")
                .setSubject(userDTO.getEmail())
                .setId(userDTO.getUserId().toString())
                // .signWith(SignatureAlgorithm.HS256, SIGNING_KEY)
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Long.valueOf(REFRESH_TOKEN_VALIDITY_MILISECONDS)))
                .compact();
    }

    /**
     * Generate access token string.
     *
     * @param authentication the authentication
     * @return the string
     * @throws Exception the exception
     */
    public String generateAccessToken(Authentication authentication)
            throws Exception {
        UserDomain userDTO = (UserDomain) authentication.getPrincipal();
        final String authorities = userDTO.getRoles().stream()
                .map(role -> "ROLE_" + role.getName())
                .collect(Collectors.joining(","));
        return Jwts.builder()
                .setSubject(userDTO.getEmail())
                .claim(AUTHORITIES_KEY, authorities)
                .setId(userDTO.getUserId().toString())
                // .signWith(SignatureAlgorithm.HS256, SIGNING_KEY)
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Long.valueOf(ACCESS_TOKEN_VALIDITY_MILISECONDS)))
                .compact();
    }

    private String generateAccessTokenNotExpiration(String authorities) throws Exception {
        return Jwts.builder()
                .setSubject("security-token")
                .setId(authorities)
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }

    /**
     * Resolve token string.
     *
     * @param req the req
     * @return String string
     * @description resolve token
     */
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader(HEADER_STRING);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Validate token boolean.
     *
     * @param token the token
     * @return String boolean
     * @description validate token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(privateKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(Error.TOKEN_INVALID.getMessage(), Error.TOKEN_INVALID.getCode(),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Get Authentication from Token
     *
     * @param token access token from request
     * @return Authentication instance
     */
    @Transactional
    public Authentication getAuthentication(String token) {
        String[] data = this.getUsernameFromToken(token).split(";");
        UserDomain userDTO = new UserDomain();

        UserEntity userEntity = usersRepository.findByEmail(data[0]);
        if (userEntity != null) {
            if (userEntity == null) {
                throw new CustomException(Error.TOKEN_INVALID.getMessage(), Error.TOKEN_INVALID.getCode(), HttpStatus.BAD_REQUEST);
            }
            userDTO.setUserId(userEntity.getId());
            userDTO.setEmail(userEntity.getEmail());
            userDTO.setPassword(userEntity.getPassword());
            RoleEntity roleEntity = roleRepository.findById(userEntity.getRoleId()).orElse(null);
            userDTO.setRoles(Arrays.asList(new RoleDomain(roleEntity.getId(), roleEntity.getName())));

            final JwtParser jwtParser = Jwts.parser().setSigningKey(privateKey);

            final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);

            final Claims claims = claimsJws.getBody();

            final Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            return new UsernamePasswordAuthenticationToken(userDTO, "", authorities);
        } else {
            throw new CustomException(Error.TOKEN_INVALID.getMessage(), Error.TOKEN_INVALID.getCode(),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Gets username from token.
     *
     * @param token the token
     * @return the username from token
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Gets user id from token.
     *
     * @param token the token
     * @return the user id from token
     */
    public String getUserIdFromToken(String token) {
        return getClaimFromToken(token, Claims::getId);
    }

    /**
     * Gets claim from token.
     *
     * @param <T>            the type parameter
     * @param token          the token
     * @param claimsResolver the claims resolver
     * @return the claim from token
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                // .setSigningKey(SIGNING_KEY)
                .setSigningKey(privateKey)
                .parseClaimsJws(token)
                .getBody();
    }

    public String encryptText(String msg) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return Base64.encodeBase64String(cipher.doFinal(msg.getBytes("UTF-8")));
    }

    public String decryptText(String msg) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(cipher.doFinal(Base64.decodeBase64(msg)), "UTF-8");
        } catch (Exception e) {
            return null;
        }
    }

    @Value("${security.key.dir}")
    public void setSECURITY_KEY_ROOT_DIR(String dir) {
        SECURITY_KEY_ROOT_DIR = dir + SECURITY_KEY_ROOT_DIR;
        SECURITY_KEY_PRIVATE_KEY = SECURITY_KEY_ROOT_DIR + SECURITY_KEY_PRIVATE_KEY;
        SECURITY_KEY_PUBLIC_KEY = SECURITY_KEY_ROOT_DIR + SECURITY_KEY_PUBLIC_KEY;
    }

    public String generateForgotPasswordToken(String email, String userId)
            throws Exception {

        return Jwts.builder()
                .setSubject(email)
                .setId(userId)
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Long.valueOf(FORGOT_PASSWORD_TOKEN_VALIDITY_MILISECONDS)))
                .compact();
    }

}
