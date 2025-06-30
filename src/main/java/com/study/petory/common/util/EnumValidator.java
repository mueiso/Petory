package com.study.petory.common.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

	private ValidEnum enumAnnotation;

	@Override
	public void initialize(ValidEnum enumAnnotation) {
		this.enumAnnotation = enumAnnotation;
	}

	@Override
	public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

		// 문자열 s의 입력값 (null, blank) 확인은 @NotBlank 가 담당
		if (s == null || s.isBlank()) {
			return true;
		}

		boolean result = false;

		// enumClass에 정의된 enum 값 전체 가져오는 과정
		Object[] enumValues = this.enumAnnotation.enumClass().getEnumConstants();

		// 입력받은 문자열 값이 enum과 같은지 확인
		if (enumValues != null) {
			for (Object enumValue : enumValues) {
				if (s.equals(enumValue.toString()) || (this.enumAnnotation.ignoreCase() && s.equalsIgnoreCase(
					enumValue.toString()))) {
					result = true;
					break;
				}
			}
		}
		return result;
	}
}
