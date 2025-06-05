package com.study.petory.exception.enums;

import org.springframework.http.HttpStatus;

public enum SuccessCode implements BaseCode {

	// COMMON
	OK(HttpStatus.OK, "요청이 성공적으로 처리되었습니다."),
	CREATED(HttpStatus.CREATED, "요청이 성공적으로 생성되었습니다."),
	DELETED(HttpStatus.OK, "요청이 성공적으로 처리되었습니다."),

	// USER
	USER_LOGIN(HttpStatus.OK, "성공적으로 로그인되었습니다."),
	USER_LOGOUT(HttpStatus.OK, "성공적으로 로그아웃되었습니다."),
	ADMIN_LOGIN(HttpStatus.OK, "성공적으로 관리자로 로그인되었습니다."),
	TOKEN_REISSUE(HttpStatus.OK, "성공적으로 토큰이 재발급되었습니다.");

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
