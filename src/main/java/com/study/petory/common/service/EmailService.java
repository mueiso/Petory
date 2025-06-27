package com.study.petory.common.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

	private static final int ACCOUNT_DELAY_DAYS = 90;

	// TODO - 배포 전 메일 발신 주소 수정 필요할지 확인
	private final String FROM_EMAIL = "noreply@petory.com";

	private final JavaMailSender mailSender;  // 이메일 전송할 때 사용하는 JavaMailSender 객체
	private final TemplateEngine templateEngine;  // HTML 템플릿을 렌더링하기 위한 Thymeleaf 템플릿 엔진


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

		// "email/deletion-warning.html" 템플릿 파일 내 ${...} 표현식에 해당 변수를 삽입하여 완성된 HTML 을 생성
		String htmlContent = templateEngine.process("email/deletion-warning", context);

		try {
			// 이메일 메시지 객체 생성
			MimeMessage message = mailSender.createMimeMessage();
			// 메시지를 쉽게 설정하기 위한 도우미 클래스 (다국어 인코딩 설정 포함)
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			/*
			 * 수신자 이메일 주소 설정
			 * 발신자 이메일 주소 설정
			 * 이메일 제목 설정
			 * 이메일 본문을 HTML 형식으로 설정 (true: HTML 전송)
			 */
			helper.setTo(to);
			helper.setFrom(FROM_EMAIL);
			helper.setSubject("[Petory] 계정 삭제 예정 안내");
			helper.setText(htmlContent, true);

			// 이메일 전송
			mailSender.send(message);
			// TODO - 이메일 전송 중 예외 발생 시, 복구(재전송) 루틴 필요
		} catch (MessagingException e) {
			throw new RuntimeException("삭제 안내 이메일 전송 실패", e);
		}
	}

	// 휴면 계정으로 전환될 예정인 유저에게 안내 이메일 발송하는 메서드
	public void sendDeactivationWarning(String to, String name, LocalDateTime updatedAt) {

		// 최종 휴면 계정으로 전환되는 날짜 (updatedAt 90일 경과)
		LocalDate deactivationDate = updatedAt.plusDays(ACCOUNT_DELAY_DAYS).toLocalDate();

		Context context = new Context();
		context.setVariable("name", name);
		context.setVariable("deactivationDate", deactivationDate);

		// "email/deactivation-warning.html" 템플릿 파일 내 ${...} 표현식에 해당 변수를 삽입하여 완성된 HTML 을 생성
		String htmlContent = templateEngine.process("email/deactivation-warning", context);

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setTo(to);
			helper.setFrom(FROM_EMAIL);
			helper.setSubject("[Petory] 장기 미접속 안내 - 곧 휴면 계정으로 전환됩니다");
			helper.setText(htmlContent, true);

			mailSender.send(message);

		} catch (MessagingException e) {
			throw new RuntimeException("휴면 안내 이메일 전송 실패", e);
		}
	}
}
