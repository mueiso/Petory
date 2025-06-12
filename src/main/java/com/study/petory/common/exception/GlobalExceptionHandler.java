package com.study.petory.common.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.common.response.CommonResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	// 유효성 예외 처리
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<CommonResponse<Map<String, String>>> validException(MethodArgumentNotValidException e) {
		log.info("Validation Fail");
		Map<String, String> errorResponse = new HashMap<>();
		e.getBindingResult().getFieldErrors()
			.forEach(error -> {
				errorResponse.put(error.getField(), error.getDefaultMessage());
			});
		return CommonResponse.of(ErrorCode.INVALID_PARAMETER, errorResponse);
	}

	// Json 타입 불량 예외 처리
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<CommonResponse<Void>> requestException(HttpMessageNotReadableException e) {
		log.info("Request Body Input Fail");
		return CommonResponse.of(ErrorCode.INVALID_INPUT, null);
	}

	// 공용 예외 처리
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<CommonResponse<Void>> CommonException(CustomException e) {
		ErrorCode response = e.getErrorCode();
		return CommonResponse.of(response, null);
	}
}
