package com.study.petory.common.config.websocket;

import java.security.Principal;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.common.security.CustomPrincipal;
import com.study.petory.common.security.JwtProvider;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

//웹소켓은 HTTP 필터를 타지 않기때문에 따로 검증해주는 클래스
@Configuration
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

	private final JwtProvider jwtProvider;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

		StompCommand command = accessor.getCommand();

		if (StompCommand.CONNECT.equals(command)) {
			String token = accessor.getFirstNativeHeader("Authorization");
			if (!StringUtils.hasText(token)) throw new CustomException(ErrorCode.NO_TOKEN);

			String rawToken = jwtProvider.subStringToken(token);
			Claims claims = jwtProvider.getClaims(rawToken);

			Long userId = Long.valueOf(claims.getSubject());
			String email = claims.get("email", String.class);
			String nickname = claims.get("nickname", String.class);
			List<SimpleGrantedAuthority> authorities = jwtProvider.getRolesFromToken(rawToken).stream()
				.map(SimpleGrantedAuthority::new)
				.toList();

			CustomPrincipal principal = new CustomPrincipal(userId, email, nickname, authorities);


			accessor.getSessionAttributes().put("user", principal);
			accessor.setUser(principal);

			accessor.setLeaveMutable(true);
			return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());

		} else if (StompCommand.SEND.equals(command) || StompCommand.SUBSCRIBE.equals(command)) {
			Object sessionUser = accessor.getSessionAttributes().get("user");
			if (sessionUser instanceof Principal principal) {
				accessor.setUser(principal);
			}

			accessor.setLeaveMutable(true);
			return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
		}

		return message;
	}

}
