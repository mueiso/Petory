package com.study.petory.common.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.study.petory.exception.enums.BaseCode;

import lombok.Getter;

@Getter
@JsonPropertyOrder({"timestamp", "message", "data"})
public class CommonResponse <T> {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private final LocalDateTime timestamp;

	private final String message;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final T data;

	public CommonResponse(BaseCode code, T data) {
		this.timestamp = LocalDateTime.now();
		this.message = code.getMessage();
		this.data = data;
	}

	public static <T> CommonResponse <T> of (BaseCode code, T data) {
		return new CommonResponse<>(code, data);
	}

	public static <T> CommonResponse <T> of (BaseCode code) {
		return new CommonResponse<>(code, null);
	}
}
