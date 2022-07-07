package com.applory.pictureserverkt.shared

import org.springframework.security.core.context.SecurityContextHolder

class SecurityUtils {
    companion object {
        @JvmStatic fun getPrincipal(): String {
            return SecurityContextHolder.getContext().authentication.principal.toString()
        }
    }
}
