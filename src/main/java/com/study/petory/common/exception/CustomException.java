package com.study.petory.common.exception;

import com.study.petory.common.exception.enums.ErrorCode;

public class CustomException extends RuntimeException {

  private final ErrorCode errorCode;

  public CustomException(ErrorCode errorCode) {

    // 메시지만 전달
    super(errorCode.getMessage());

    // 에서 상태코드는 따로 필드로 보관
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
