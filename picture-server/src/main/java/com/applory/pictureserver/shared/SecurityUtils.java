package com.applory.pictureserver.shared;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public class SecurityUtils {
    public static String getPrincipal() {
        if (Objects.nonNull(SecurityContextHolder.getContext().getAuthentication())) {
            return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }

        return null;
    }
}
