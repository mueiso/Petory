package com.study.petory.exception.enums;

import org.springframework.http.HttpStatus;

public interface BaseCode {

	HttpStatus getStatus();

	String getMessage();
}