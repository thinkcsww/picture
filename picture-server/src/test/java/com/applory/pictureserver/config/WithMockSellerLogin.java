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
@WithSecurityContext(factory = WithMockSellerLogin.WithMockLoginSecurityContextFactory.class)
public @interface WithMockSellerLogin {
    class WithMockLoginSecurityContextFactory implements WithSecurityContextFactory<WithMockSellerLogin> {
        @Override
        public SecurityContext createSecurityContext(WithMockSellerLogin customUser) {
            SecurityContext context = SecurityContextHolder.createEmptyContext();

            Authentication auth = new UsernamePasswordAuthenticationToken(TestConstants.TEST_SELLER_USERNAME, null, AuthorityUtils.createAuthorityList("ROLE_USER"));
            context.setAuthentication(auth);

            return context;
        }
    }
}
