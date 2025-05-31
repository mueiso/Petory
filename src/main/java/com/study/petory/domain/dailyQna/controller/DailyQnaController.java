package com.study.petory.domain.dailyQna.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.response.CommonResponse;
import com.study.petory.domain.dailyQna.dto.request.DailyQNACreateRequestDto;
import com.study.petory.domain.dailyQna.service.DailyQnaService;
import com.study.petory.exception.enums.SuccessCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/daily-qnas")
@RequiredArgsConstructor
public class DailyQnaController {

	private final DailyQnaService dailyQnaService;

	/**
	 * 오늘의 질문 답변 등록
	 * userId				답변을 작성한 유저
	 * @param questionId	유저가 답변을 작성한 질문의 Id
	 * @param request 		유저가 작성한 답변
	 * @return CommonResponse 성공 메세지, data
	 */
	@PostMapping("/{questionId}")
	public CommonResponse<Void> createDailyQna(
		// 유저 정보: 수정 예정
		// 어노테이션 Long userId
		@PathVariable Long questionId,
		@Valid @RequestBody DailyQNACreateRequestDto request) {
		Long userId = 1L;
		dailyQnaService.saveDailyQNA(userId, questionId, request);
		return CommonResponse.of(SuccessCode.CREATED);
	}
}
