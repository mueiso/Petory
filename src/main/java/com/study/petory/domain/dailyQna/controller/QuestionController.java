package com.study.petory.domain.dailyQna.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.response.CommonResponse;
import com.study.petory.domain.dailyQna.dto.request.QuestionCreateRequestDto;
import com.study.petory.domain.dailyQna.service.QuestionService;
import com.study.petory.exception.enums.SuccessCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/questions")
public class QuestionController {

	private final QuestionService questionService;

	/**
	 * 질문 생성
	 * userId				답변을 작성한 유저
	 * @param request		관리자가 추가하는 질문
	 * @return	CommonResponse 성공 메세지, data: null
	 */
	@PostMapping
	public ResponseEntity<CommonResponse<Void>> createQuestion(
		// 유저 정보: 수정 예정
		// 어노테이션 Long userId
		@Valid @RequestBody QuestionCreateRequestDto request
	) {
		Long userId = 1L;
		questionService.saveQuestion(userId, request);
		return CommonResponse.of(SuccessCode.CREATED);
	}
}
