package com.example.hourlymaids.util;

import com.example.hourlymaids.constant.CustomException;
import com.example.hourlymaids.constant.Error;
import com.example.hourlymaids.domain.UserDomain;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * The type User util.
 */
public final class UserUtils {

    public static UserDomain getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDomain) {
            return (UserDomain) authentication.getPrincipal();
        }

        throw new CustomException(Error.BAD_CREDENTIALS.getMessage(), Error.BAD_CREDENTIALS.getCode(),
                HttpStatus.UNAUTHORIZED);
    }

    /**
     * Gets current user id.
     *
     * @return the current user id
     */
    public static Long getCurrentUserId() {
        return getCurrentUser().getUserId();
    }

    /**
     * Gets current username.
     *
     * @return the current username
     */
    public static String getCurrentUsername() {
        return getCurrentUser().getFullName();
    }

    /**
     * Gets current password.
     *
     * @return the current password
     */
    public static String getCurrentPassword() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return (String) authentication.getCredentials();
        }

        throw new CustomException(Error.BAD_CREDENTIALS.getMessage(), Error.BAD_CREDENTIALS.getCode(),
                HttpStatus.UNAUTHORIZED);
    }
}