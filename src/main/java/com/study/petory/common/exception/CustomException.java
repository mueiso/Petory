package com.study.petory.common.exception;

import com.study.petory.common.exception.enums.ErrorCode;

public class CustomException extends RuntimeException {

	private final ErrorCode errorCode;

	public CustomException(ErrorCode errorCode) {

		super(errorCode.getMessage());

		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
