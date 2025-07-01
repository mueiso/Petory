package com.study.petory.common.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidEnum, Enum> {

	private ValidEnum enumAnnotation;

	@Override
	public void initialize(ValidEnum enumAnnotation) {
		this.enumAnnotation = enumAnnotation;
	}

	@Override
	public boolean isValid(Enum e, ConstraintValidatorContext constraintValidatorContext) {

		// Enum e의 입력값 (null, blank) 확인은 @NotNull 이 담당
		if (e == null) {
			return true;
		}

		boolean result = false;

		// enumClass에 정의된 enum 값 전체 가져오는 과정
		Object[] enumValues = this.enumAnnotation.enumClass().getEnumConstants();

		// 입력받은 Enum 값이 DB의 Enum과 같은지 확인
		if (enumValues != null) {
			for (Object enumValue : enumValues) {
				if (e.equals(enumValue)) {
					result = true;
					break;
				}
			}
		}
		return result;
	}
}
