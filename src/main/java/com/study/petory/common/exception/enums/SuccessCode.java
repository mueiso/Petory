package com.study.petory.common.exception.enums;

import org.springframework.http.HttpStatus;

public enum SuccessCode implements BaseCode {

	// Common
	REQUESTED(HttpStatus.OK, "SuccessCode를 수정해주세요"),
	CREATED(HttpStatus.CREATED, "성공적으로 생성되었습니다."),
	FOUND(HttpStatus.OK, "성공적으로 조회되었습니다."),
	UPDATED(HttpStatus.OK, "성공적으로 수정되었습니다."),
	DELETED(HttpStatus.OK, "성공적으로 삭제되었습니다."),
	RESTORED(HttpStatus.OK, "성공적으로 복구되었습니다."),

	// USER
	USER_LOGIN(HttpStatus.OK, "성공적으로 로그인되었습니다."),
	USER_LOGOUT(HttpStatus.OK, "성공적으로 로그아웃되었습니다."),
	ADMIN_LOGIN(HttpStatus.OK, "성공적으로 관리자로 로그인되었습니다."),
	TOKEN_REISSUE(HttpStatus.OK, "성공적으로 토큰이 재발급되었습니다."),
	USER_DELETED(HttpStatus.OK, "성공적으로 탈퇴되었습니다."),
	EMAIL_SENT(HttpStatus.OK, "인증 코드가 이메일로 전송되었습니다."),
	VERIFIED(HttpStatus.OK, "성공적으로 인증되었습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	SuccessCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	@Override
	public HttpStatus getStatus() {
		return httpStatus;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
