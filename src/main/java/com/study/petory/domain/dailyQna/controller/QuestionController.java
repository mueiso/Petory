package com.study.petory.domain.dailyQna.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.response.CommonResponse;
import com.study.petory.domain.dailyQna.dto.request.DailyQnaCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.request.DailyQnaUpdateRequestDto;
import com.study.petory.domain.dailyQna.dto.request.QuestionCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.response.DailyQnaGetResponseDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetAllResponseDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetOneResponseDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetTodayResponseDto;
import com.study.petory.domain.dailyQna.service.DailyQnaService;
import com.study.petory.domain.dailyQna.service.QuestionService;
import com.study.petory.exception.enums.SuccessCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {

	private final DailyQnaService dailyQnaService;
	private final QuestionService questionService;

	@PostMapping("/test")
	public ResponseEntity<CommonResponse<Void>> test() {
		questionService.setQuestion();
		return CommonResponse.of(SuccessCode.OK);
	}

	/**
	 * 질문 생성				관리자만 가능
	 * userId				관리자인지 검증
	 * @param request       관리자가 추가하는 질문
	 * @return CommonResponse 성공 메세지, data: null
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

	/**
	 * 질문 전체 조회			관리자만 가능
	 * userId				관리자인지 검증
	 * @param page          한 번에 50개씩 반환하여 원하는 페이지를 선택
	 * @return CommonResponse 성공 메세지, data: 질문을 날짜 기준 내림차순으로 조회, 한 페이지에 50개씩 출력
	 */
	@GetMapping("/all")
	public ResponseEntity<CommonResponse<Page<QuestionGetAllResponseDto>>> getAllQuestion(
		// 유저 정보: 수정 예정
		// 어노테이션 Long userId
		@RequestParam(defaultValue = "1") int page
	) {
		Long userId = 1L;
		return CommonResponse.of(SuccessCode.OK, questionService.getAllQuestion(userId, page));
	}

	/**
	 * 질문 단건 조회			관리자만 가능
	 * userId				관리자인지 검증
	 * @param questionId	조회할 질문의 id
	 * @return	CommonResponse 성공 메세지, data: 질문, 날짜
	 */
	@GetMapping("/{questionId}")
	public ResponseEntity<CommonResponse<QuestionGetOneResponseDto>> getOneQuestion(
		// 유저 정보: 수정 예정
		// 어노테이션 Long userId
		@PathVariable Long questionId
	) {
		Long userId = 1L;
		return CommonResponse.of(SuccessCode.OK, questionService.getOneQuestion(userId, questionId));
	}

	/**
	 * 오늘의 질문 조회		모든 권한 사용 가능
	 * @return	CommonResponse 성공 메세지, data: 질문, 날짜
	 */
	@GetMapping("/today")
	public ResponseEntity<CommonResponse<QuestionGetTodayResponseDto>> getTodayQuestion(
	) {
		return CommonResponse.of(SuccessCode.OK, questionService.getTodayQuestion());
	}

	/**
	 * 답변 등록
	 * userId				답변을 작성한 유저
	 * @param questionId    유저가 답변을 작성한 질문의 Id
	 * @param request        유저가 작성한 답변
	 * @return CommonResponse 성공 메세지, data: null
	 */
	@PostMapping("/{questionId}/daily-qnas")
	public ResponseEntity<CommonResponse<Void>> createDailyQna(
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
	 * 답변 조회
	 * userId				답변을 작성한 유저
	 * @param questionId    질문 Id로 유저가 작성한 답변을 검색
	 * @return CommonResponse 성공 메세지, data: 작성일 기준 내림차순 답변 조회
	 */
	@GetMapping("/{questionId}/daily-qnas")
	public ResponseEntity<CommonResponse<List<DailyQnaGetResponseDto>>> getDailyQna(
		// 유저 정보: 수정 예정
		// 어노테이션 Long userId
		@PathVariable Long questionId
	) {
		Long userId = 1L;
		return CommonResponse.of(SuccessCode.OK, dailyQnaService.findDailyQna(userId, questionId));
	}

	/**
	 * 답변 수정
	 * userId				답변을 작성한 유저
	 * @param dailyQnaId    유저가 수정할 답변의 id
	 * @param request        유저가 작성한 수정 할 내용
	 * @return CommonResponse 성공 메세지, data: null
	 */
	@PatchMapping("/{questionId}/daily-qnas/{dailyQnaId}")
	public ResponseEntity<CommonResponse<Void>> updateDailyQna(
		// 유저 정보: 수정 예정
		// 어노테이션 Long userId
		@PathVariable Long questionId,
		@PathVariable Long dailyQnaId,
		@Valid @RequestBody DailyQnaUpdateRequestDto request
	) {
		Long userId = 1L;
		dailyQnaService.updateDailyQna(userId, questionId, dailyQnaId, request);
		return CommonResponse.of(SuccessCode.OK);
	}

	/**
	 * 답변 삭제
	 * userId				답변을 작성한 유저
	 * @param dailyQnaId    유저가 삭제할 답변의 id
	 * @return CommonResponse 성공 메세지, data: null
	 */
	@DeleteMapping("/{questionId}/daily-qnas/{dailyQnaId}")
	public ResponseEntity<CommonResponse<Void>> deleteDailyQna(
		// 유저 정보: 수정 예정
		// 어노테이션 Long userId
		@PathVariable Long questionId,
		@PathVariable Long dailyQnaId
	) {
		Long userId = 1L;
		dailyQnaService.deleteDailyQna(userId, questionId, dailyQnaId);
		return CommonResponse.of(SuccessCode.OK);
	}
}
