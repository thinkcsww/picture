package com.applory.pictureserver.domain.interceptor;

import com.applory.pictureserver.domain.config.JwtTokenProvider;
import com.applory.pictureserver.domain.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;

@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Component
@Slf4j
public class StompChannelInterceptor implements ChannelInterceptor {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        log.debug("Command:" + accessor.getCommand());
        log.debug("Message: " + message);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = Objects.requireNonNull(accessor.getFirstNativeHeader("Authorization")).substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                log.info("==== Stomp Connect Success ====");
                Authentication auth = jwtTokenProvider.getAuthentication(token);    // 인증 객체 생성
                accessor.setUser(auth);
            } else {
                throw new UnauthorizedException("Invalid Token");
            }
        }
        return message;
    }
}
