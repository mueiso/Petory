package com.study.petory.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {

		registry.addEndpoint("/ws-chat") //클라이언트가 웹소켓 연결을 위해 마지막에 붙혀야하는 경로
			.setAllowedOriginPatterns("*") //CORS 설정(테스트용으로 전체 허용이나 배포 시 변경 필요)
			.withSockJS(); //호환성을 높이기 위해 JS 사용(없을경우 http/1.1 이하에서 사용 불가)
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {

		//prefix
		registry.enableSimpleBroker("/sub"); //서버가 구독자에게 메세지 보내는 경로

		registry.setApplicationDestinationPrefixes("/pub"); //클라이언트가 서버에 메시지 보내는 경로
	}
}
