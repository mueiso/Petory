package com.study.petory.common.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender mailSender;  // 이메일 전송할 때 사용하는 JavaMailSender 객체
	private final TemplateEngine templateEngine;  // HTML 템플릿을 렌더링하기 위한 Thymeleaf 템플릿 엔진

	@Value("${mail.from}")
	private String FROM_EMAIL;

	private static final int ACCOUNT_DELAY_DAYS = 90;
	private static final int MAX_RETRY = 10;

	// soft delete 되어있는 계정에게 삭제 경고 이메일을 발송하는 메서드
	public void sendDeletionWarning(String to, String name, LocalDateTime deletedAt) {

		// 최종 삭제될 날짜 (soft delete 된 후 90일 경과)
		LocalDate deletionDate = deletedAt.plusDays(ACCOUNT_DELAY_DAYS).toLocalDate();
		// soft delete 된 날짜
		LocalDate deactivatedDate = deletedAt.toLocalDate();

		/*
		 * 템플릿에 전달할 데이터를 담을 Context 객체 생성
		 * 사용자 이름을 템플릿 변수로 설정
		 * 휴면 시작일을 템플릿 변수로 설정
		 * 삭제 예정일을 템플릿 변수로 설정
		 */
		Context context = new Context();
		context.setVariable("name", name);
		context.setVariable("deactivatedDate", deactivatedDate);
		context.setVariable("deletionDate", deletionDate);

		sendEmail(to, "[Petory] 계정 삭제 예정 안내", "email/deletion-warning", context);
	}

	// 휴면 계정으로 전환될 예정인 유저에게 안내 이메일 발송하는 메서드
	public void sendDeactivationWarning(String to, String name, LocalDateTime updatedAt) {

		// 최종 휴면 계정으로 전환되는 날짜 (updatedAt 90일 경과)
		LocalDate deactivationDate = updatedAt.plusDays(ACCOUNT_DELAY_DAYS).toLocalDate();

		Context context = new Context();
		context.setVariable("name", name);
		context.setVariable("deactivationDate", deactivationDate);

		sendEmail(to, "[Petory] 장기 미접속 안내 - 곧 휴면 계정으로 전환됩니다", "email/deactivation-warning", context);
	}

	// 중복 로직 줄이기 위한 공통 이메일 발송 메서드
	private void sendEmail(String to, String subject, String templatePath, Context context) {

		int attempt = 0;
		while (attempt < MAX_RETRY) {
			try {
				// 이메일 메시지 객체 생성
				MimeMessage message = mailSender.createMimeMessage();
				// 메시지를 쉽게 설정하기 위한 도우미 클래스 (다국어 인코딩 설정 포함)
				MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

				// "email/deletion-warning.html" 템플릿 파일 내 ${...} 표현식에 해당 변수를 삽입하여 완성된 HTML 을 생성
				String htmlContent = templateEngine.process(templatePath, context);

				/*
				 * 수신자 이메일 주소 설정
				 * 발신자 이메일 주소 설정
				 * 이메일 제목 설정
				 * 이메일 본문을 HTML 형식으로 설정 (true: HTML 전송)
				 */
				helper.setTo(to);
				helper.setFrom(FROM_EMAIL);
				helper.setSubject(subject);
				helper.setText(htmlContent, true);

				// 이메일 전송
				mailSender.send(message);
				log.info("이메일 전송 성공 - to: {}. subject: {}", to, subject);

				return;

			} catch (MessagingException e) {
				attempt++;
				log.warn(
					"이메일 전송 실패. (시도 {} / {} - to: {}, subject: {}, error: {}",
					attempt, MAX_RETRY, to, subject, e.getMessage());

				/*
				 * 메일 전송 실패 시 약간의 딜레이를 준 뒤 다시 시도
				 * SMTP 서버 과부하 방지: 연속 시도 시 Gmail 등에서 IP 차단 가능성 생김 방지
				 * 네트워크 오류 완화: 일시적인 네트워크 오류인 경우 수 밀리초 후 재시도 시 성공하는 경우 많기 때문
				 * 외부 서비스의 속도 제한 대응: 일부 SMTP 서버는 짧은 시간에 다량 요청하는 것을 제한하기 때문에, 이를 방지하기 위함
				 */
				try {
					Thread.sleep(3000L);  // 3초
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();  // interrupt 상태 복구
				}
			}
		}
		log.error("이메일 전송 최종 실패 - to: {}, subject: {}", to, subject);
		throw new RuntimeException("이메일 전송 실패 - to: " + to + ", subject: " + subject);
	}
}
