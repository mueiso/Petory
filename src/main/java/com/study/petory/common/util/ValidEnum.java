package com.study.petory.common.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD}) // 필드에 대한 어노테이션을 지정하기 위함
@Retention(RetentionPolicy.RUNTIME) // 어노테이션을 Runtime 까지 유지
@Constraint(validatedBy = EnumValidator.class) // 구현체 지정
public @interface ValidEnum {

	String message() default "잘못된 Enum 값입니다.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	Class<? extends java.lang.Enum<?>> enumClass();

	boolean ignoreCase() default false;
}
