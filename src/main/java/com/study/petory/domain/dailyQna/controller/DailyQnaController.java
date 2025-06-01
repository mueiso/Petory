package com.study.petory.domain.dailyQna.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.response.CommonResponse;
import com.study.petory.domain.dailyQna.dto.request.DailyQnaCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.request.DailyQnaUpdateRequestDto;
import com.study.petory.domain.dailyQna.dto.response.DailyQnaGetResponseDto;
import com.study.petory.domain.dailyQna.service.DailyQnaService;
import com.study.petory.exception.enums.SuccessCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class DailyQnaController {

	private final DailyQnaService dailyQnaService;

	/**
	 * 오늘의 질문 답변 등록
	 * userId				답변을 작성한 유저
	 * @param questionId	유저가 답변을 작성한 질문의 Id
	 * @param request 		유저가 작성한 답변
	 * @return CommonResponse 성공 메세지, data: null
	 */
	@PostMapping("/{questionId}/daily-qnas")
	public CommonResponse<Void> createDailyQna(
		// 유저 정보: 수정 예정
		// 어노테이션 Long userId
		@PathVariable Long questionId,
		@Valid @RequestBody DailyQnaCreateRequestDto request
	) {
		Long userId = 1L;
		dailyQnaService.saveDailyQna(userId, questionId, request);
		return CommonResponse.of(SuccessCode.CREATED);
	}

	/**
	 * 질문에 대한 사용자의 답변 조회
	 * userId				답변을 작성한 유저
	 * @param questionId	질문 Id로 유저가 작성한 답변을 검색
	 * @return CommonResponse 성공 메세지, data: 작성일 기준 내림차순 답변 조회
	 */
	@GetMapping("/{questionId}/daily-qnas")
	public CommonResponse<List<DailyQnaGetResponseDto>> getDailyQna(
		// 유저 정보: 수정 예정
		// 어노테이션 Long userId
		@PathVariable Long questionId
	) {
		Long userId = 1L;
		return CommonResponse.of(SuccessCode.OK, dailyQnaService.findDailyQna(userId, questionId));
	}

	/**
	 * 답변을 사용자가 수정
	 * userId				답변을 작성한 유저
	 * @param dailyQnaId	유저가 수정할 답변의 id
	 * @param request		유저가 작성한 수정 할 내용
	 * @return	CommonResponse 성공 메세지, data: null
	 */
	@PatchMapping("/daily-qnas/{dailyQnaId}")
	public CommonResponse<Void> updateDailyQna(
		// 유저 정보: 수정 예정
		// 어노테이션 Long userId
		@PathVariable Long dailyQnaId,
		@Valid @RequestBody DailyQnaUpdateRequestDto request
	) {
		Long userId = 1L;
		dailyQnaService.updateDailyQna(userId, dailyQnaId, request);
		return CommonResponse.of(SuccessCode.OK);
	}
}
