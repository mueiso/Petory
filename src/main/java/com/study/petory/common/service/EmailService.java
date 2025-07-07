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

	private final JavaMailSender mailSender;
	private final TemplateEngine templateEngine;

	@Value("${mail.from}")
	private String FROM_EMAIL;

	private static final int ACCOUNT_DELAY_DAYS = 90;
	private static final int MAX_RETRY = 10;

	// soft delete 되어있는 계정에게 삭제 경고 이메일을 발송하는 메서드
	public void sendDeletionWarning(String to, String name, LocalDateTime deletedAt) {

		LocalDate deletionDate = deletedAt.plusDays(ACCOUNT_DELAY_DAYS).toLocalDate();
		LocalDate deactivatedDate = deletedAt.toLocalDate();

		Context context = new Context();
		context.setVariable("name", name);
		context.setVariable("deactivatedDate", deactivatedDate);
		context.setVariable("deletionDate", deletionDate);

		sendEmail(to, "[Petory] 계정 삭제 예정 안내", "email/deletion-warning", context);
	}

	// 휴면 계정으로 전환될 예정인 유저에게 안내 이메일 발송하는 메서드
	public void sendDeactivationWarning(String to, String name, LocalDateTime updatedAt) {

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

				// 메일 전송 실패 시 약간의 딜레이를 준 뒤 다시 시도
				try {
					Thread.sleep(3000L);  // 3초
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
				}
			}
		}
		log.error("이메일 전송 최종 실패 - to: {}, subject: {}", to, subject);
		throw new RuntimeException("이메일 전송 실패 - to: " + to + ", subject: " + subject);
	}
}
