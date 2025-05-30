package com.study.petory.exception.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode implements BaseCode {

	// common
	INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 파라미터 요청입니다."),
	INVALID_INPUT(HttpStatus.BAD_REQUEST, "요청한 값에 오류가 있습니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요한 요청입니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 필요한 요청입니다."),
	NO_RESOURCE(HttpStatus.NOT_FOUND, "존재하지 않는 데이터입니다."),
	EXIST_RESOURCE(HttpStatus.CONFLICT, "중복된 데이터입니다."),
	LONG_JSON_TYPE(HttpStatus.BAD_REQUEST, "요청 형식이 잘못되었습니다. JSON 구조를 확인하세요."),

	// user
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다."),

	// pet
	PET_NOT_FOUND(HttpStatus.NOT_FOUND, "펫이 존재하지 않습니다."),

	;

	private final HttpStatus httpStatus;
	private final String message;

	ErrorCode(HttpStatus httpStatus, String message) {
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
