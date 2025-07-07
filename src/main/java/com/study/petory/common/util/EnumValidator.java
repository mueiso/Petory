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

		if (e == null) {
			return true;
		}

		boolean result = false;

		Object[] enumValues = this.enumAnnotation.enumClass().getEnumConstants();

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
