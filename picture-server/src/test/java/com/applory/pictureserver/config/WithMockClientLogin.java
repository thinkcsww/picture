package com.applory.pictureserver.config;

import com.applory.pictureserver.TestConstants;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockClientLogin.WithMockLoginSecurityContextFactory.class)
public @interface WithMockClientLogin {
    class WithMockLoginSecurityContextFactory implements WithSecurityContextFactory<WithMockClientLogin> {
        @Override
        public SecurityContext createSecurityContext(WithMockClientLogin customUser) {
            SecurityContext context = SecurityContextHolder.createEmptyContext();

            Authentication auth = new UsernamePasswordAuthenticationToken(TestConstants.TEST_CLIENT_USERNAME, null, AuthorityUtils.createAuthorityList("ROLE_USER"));
            context.setAuthentication(auth);

            return context;
        }
    }
}
