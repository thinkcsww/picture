package com.applory.pictureserver.config;

import com.applory.pictureserver.domain.user.CustomUserDetailService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private String secretKey;

    private final CustomUserDetailService userDetailsService;

    private final AppConfiguration appConfiguration;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(appConfiguration.getJwtSignKey().getBytes(StandardCharsets.UTF_8));
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserName(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // Jwt Token에서 username 추출
    public String getUserName(String token) {
        return (String) Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("user_name");
    }

    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(jwtToken);
            return !claims
                    .getBody()
                    .getExpiration()
                    .before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean refreshTokenIsNotExpireIn7Days(String jwtToken) {
        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(jwtToken);
            return !claims
                    .getBody()
                    .getExpiration()
                    .before(new Date(System.currentTimeMillis() + (23 * DAY_IN_MS)));
        } catch (Exception e) {
            return false;
        }
    }

}
