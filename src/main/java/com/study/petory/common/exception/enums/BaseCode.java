package com.study.petory.common.exception.enums;

import org.springframework.http.HttpStatus;

public interface BaseCode {

	HttpStatus getStatus();

	String getMessage();
}