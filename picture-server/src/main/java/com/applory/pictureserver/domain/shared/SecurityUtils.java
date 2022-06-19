package com.applory.pictureserver.domain.shared;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static String getPrincipal() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
