package com.study.petory.common.security;

import java.net.http.HttpHeaders;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;

import lombok.RequiredArgsConstructor;

//웹소켓은 HTTP 필터를 타지 않기때문에 따로 검증해주는 클래스
@Configuration
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

	private final JwtProvider jwtProvider;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {

		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

		if (accessor.getCommand() == StompCommand.CONNECT) {
			validateToken(accessor);
		}

		return message;
	}

	private void validateToken(StompHeaderAccessor accessor) {

		String accessToken = accessor.getFirstNativeHeader("Authorization");

		if (accessToken == null) {
			throw new CustomException(ErrorCode.INVALID_TOKEN);
		}

		jwtProvider.getClaims(accessToken);
	}
}
