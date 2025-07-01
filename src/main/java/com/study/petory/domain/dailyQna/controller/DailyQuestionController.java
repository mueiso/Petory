package com.study.petory.domain.dailyQna.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.exception.enums.SuccessCode;
import com.study.petory.common.response.CommonResponse;
import com.study.petory.common.security.CustomPrincipal;
import com.study.petory.domain.dailyQna.dto.request.DailyAnswerCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.request.DailyAnswerUpdateRequestDto;
import com.study.petory.domain.dailyQna.dto.request.DailyQuestionCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.request.DailyQuestionUpdateRequestDto;
import com.study.petory.domain.dailyQna.dto.response.DailyAnswerGetDeletedResponse;
import com.study.petory.domain.dailyQna.dto.response.DailyAnswerGetHiddenResponseDto;
import com.study.petory.domain.dailyQna.dto.response.DailyAnswerGetResponseDto;
import com.study.petory.domain.dailyQna.dto.response.DailyQuestionGetAllResponseDto;
import com.study.petory.domain.dailyQna.dto.response.DailyQuestionGetDeletedResponseDto;
import com.study.petory.domain.dailyQna.dto.response.DailyQuestionGetInactiveResponseDto;
import com.study.petory.domain.dailyQna.dto.response.DailyQuestionGetOneResponseDto;
import com.study.petory.domain.dailyQna.dto.response.DailyQuestionGetTodayResponseDto;
import com.study.petory.domain.dailyQna.service.DailyAnswerService;
import com.study.petory.domain.dailyQna.service.DailyQuestionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/daily-questions")
@RequiredArgsConstructor
public class DailyQuestionController {

	private final DailyAnswerService dailyAnswerService;
	private final DailyQuestionService dailyQuestionService;

	/**
	 * 질문 생성					관리자만 가능
	 * @param request      	 	관리자가 추가하는 질문
	 * @return CommonResponse 성공 메세지, data: null
	 */
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CommonResponse<Void>> createDailyQuestion(
		@Valid @RequestBody DailyQuestionCreateRequestDto request
	) {
		dailyQuestionService.saveDailyQuestion(request);
		return CommonResponse.of(SuccessCode.CREATED);
	}

	/**
	 * 질문 전체 조회				관리자만 가능
	 * @param pageable			한 번에 50개씩 반환하여 원하는 페이지를 선택
	 * @return CommonResponse 성공 메세지, data: 질문, 날짜, 비활성화 시 비활성화 날짜 표시
	 * 											질문을 날짜 기준 내림차순으로 조회, 한 페이지에 50개씩 출력
	 */
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CommonResponse<Page<DailyQuestionGetAllResponseDto>>> getAllDailyQuestion(
		@PageableDefault(size = 50, sort = "date", direction = Sort.Direction.ASC) Pageable pageable
	) {
		return CommonResponse.of(SuccessCode.FOUND, dailyQuestionService.findAllDailyQuestion(pageable));
	}

	/**
	 * 질문 단건 조회				관리자만 가능
	 * @param dailyQuestionId	조회할 질문의 id
	 * @return	CommonResponse 성공 메세지, data: 질문, 날짜, 비활성화 시 비활성화 날짜 표시
	 */
	@GetMapping("/{dailyQuestionId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CommonResponse<DailyQuestionGetOneResponseDto>> getOneDailyQuestion(
		@PathVariable Long dailyQuestionId
	) {
		return CommonResponse.of(SuccessCode.FOUND, dailyQuestionService.findOneDailyQuestion(dailyQuestionId));
	}

	/**
	 * 오늘의 질문 조회			모든 권한 사용 가능
	 * @return	CommonResponse 성공 메세지, data: 질문, 날짜
	 */
	@GetMapping("/today")
	public ResponseEntity<CommonResponse<DailyQuestionGetTodayResponseDto>> getTodayDailyQuestion(
	) {
		return CommonResponse.of(SuccessCode.FOUND, dailyQuestionService.findTodayDailyQuestion());
	}

	/**
	 * 질문 수정					관리자만 가능
	 * @param dailyQuestionId	수정할 질문의 id
	 * @param request			수정할 내용
	 * @return	CommonResponse 성공 메세지, data: null
	 */
	@PutMapping("/{dailyQuestionId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CommonResponse<Void>> updateDailyQuestion(
		@PathVariable Long dailyQuestionId,
		@Valid @RequestBody DailyQuestionUpdateRequestDto request
		) {
		dailyQuestionService.updateDailyQuestion(dailyQuestionId, request);
		return CommonResponse.of(SuccessCode.UPDATED);
	}

	/**
	 * 질문 비활성화				관리자만 가능
	 * @param dailyQuestionId	비활성화 할 질문의 id
	 * @return	CommonResponse 성공 메세지, data: null
	 */
	@PatchMapping("/{dailyQuestionId}/inactive")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CommonResponse<Void>> InactiveDailyQuestion(
		@PathVariable Long dailyQuestionId
	) {
		dailyQuestionService.inactiveDailyQuestion(dailyQuestionId);
		return CommonResponse.of(SuccessCode.UPDATED);
	}

	/**
	 * 비활성화 된 질문 조회		관리자만 가능
	 * @param pageable			한 번에 50개씩 반환하여 원하는 페이지를 선택
	 * @return	CommonResponse 성공 메세지, data: 질문, 날짜, 수정일
	 */
	@GetMapping("/inactive")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CommonResponse<Page<DailyQuestionGetInactiveResponseDto>>> getInactiveDailyQuestion(
		@PageableDefault(size = 50, sort = "updatedAt", direction = Sort.Direction.ASC) Pageable pageable
	) {
		return CommonResponse.of(SuccessCode.FOUND, dailyQuestionService.findInactiveDailyQuestion(pageable));
	}

	/**
	 * 비활성화 된 질문 활성화		관리자만 가능
	 * @param dailyQuestionId	활성화 할 질문의 id
	 * @return	CommonResponse 성공 메세지, data: null
	 */
	@PatchMapping("/{dailyQuestionId}/activate/restore")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CommonResponse<Void>> updateDailyQuestionStatusActive(
		@PathVariable Long dailyQuestionId
	) {
		dailyQuestionService.updateDailyQuestionStatusActive(dailyQuestionId);
		return CommonResponse.of(SuccessCode.UPDATED);
	}

	/**
	 * 질문 삭제					관리자만 가능
	 * @param dailyQuestionId	삭제 할 질문의 id
	 * @return	CommonResponse 성공 메세지, data: null
	 */
	@DeleteMapping("/{dailyQuestionId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CommonResponse<Void>> deactivateDailyQuestion(
		@PathVariable Long dailyQuestionId
	) {
		dailyQuestionService.deactivateDailyQuestion(dailyQuestionId);
		return CommonResponse.of(SuccessCode.DELETED);
	}

	/**
	 * 삭제된 질문 조회			관리자만 가능
	 * @param pageable			한 번에 50개씩 반환하여 원하는 페이지를 선택
	 * @return	CommonResponse 성공 메세지, data: 질문, 날짜, 삭제일
	 */
	@GetMapping("/deleted")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CommonResponse<Page<DailyQuestionGetDeletedResponseDto>>> getDailyQuestionByDeleted(
		@PageableDefault(size = 50, sort = "deletedAt", direction = Sort.Direction.ASC) Pageable pageable
	) {
		return CommonResponse.of(SuccessCode.FOUND, dailyQuestionService.findDailyQuestionByDeleted(pageable));
	}

	/**
	 * 질문 복구				관리자만 가능
	 * @param dailyQuestionId	복구 할 질문의 id
	 * @return	CommonResponse 성공 메세지, data: null
	 */
	@PatchMapping("/{dailyQuestionId}/restore")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CommonResponse<Void>> restoreDailyQuestion(
		@PathVariable Long dailyQuestionId
	) {
		dailyQuestionService.restoreDailyQuestion(dailyQuestionId);
		return CommonResponse.of(SuccessCode.RESTORED);
	}

	/**
	 * 답변 등록
	 * @param currentUser		답변을 작성한 유저
	 * @param dailyQuestionId   유저가 답변을 작성한 질문의 Id
	 * @param request       	유저가 작성한 답변
	 * @return CommonResponse 성공 메세지, data: null
	 */
	@PostMapping("/{dailyQuestionId}/daily-answers")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<CommonResponse<Void>> createDailyAnswer(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long dailyQuestionId,
		@Valid @RequestBody DailyAnswerCreateRequestDto request
	) {
		dailyAnswerService.saveDailyAnswer(currentUser.getId(), dailyQuestionId, request);
		return CommonResponse.of(SuccessCode.CREATED);
	}

	/**
	 * 질문의 답변 조회
	 * @param currentUser		답변을 작성한 유저
	 * @param dailyQuestionId	질문 Id로 유저가 작성한 답변을 검색
	 * @return CommonResponse 성공 메세지, data: 작성일 기준 내림차순 답변 조회
	 */
	@GetMapping("/{dailyQuestionId}/daily-answers")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<CommonResponse<List<DailyAnswerGetResponseDto>>> getDailyAnswer(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long dailyQuestionId
	) {
		return CommonResponse.of(SuccessCode.FOUND,
			dailyAnswerService.findDailyAnswer(currentUser.getId(), dailyQuestionId));
	}

	/**
	 * 답변 수정
	 * @param currentUser		답변을 작성한 유저
	 * @param dailyAnswerId		유저가 수정할 답변의 id
	 * @param request			유저가 작성한 수정 할 내용
	 * @return CommonResponse 성공 메세지, data: null
	 */
	@PutMapping("/daily-answers/{dailyAnswerId}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<CommonResponse<Void>> updateDailyAnswer(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long dailyAnswerId,
		@Valid @RequestBody DailyAnswerUpdateRequestDto request
	) {
		dailyAnswerService.updateDailyAnswer(currentUser.getId(), dailyAnswerId, request);
		return CommonResponse.of(SuccessCode.UPDATED);
	}

	/**
	 * 답변 숨김
	 * @param currentUser		답변을 작성한 유저
	 * @param dailyAnswerId		숨길 답변의 id
	 * @return	CommonResponse 성공 메세지, data: null
	 */
	@PatchMapping("/daily-answers/{dailyAnswerId}/hide")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<CommonResponse<Void>> hideDailyAnswer(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long dailyAnswerId
	) {
		dailyAnswerService.hideDailyAnswer(currentUser.getId(), dailyAnswerId);
		return CommonResponse.of(SuccessCode.UPDATED);
	}

	/**
	 * 숨긴 답변 조회
	 * @param currentUser		삭제할 답변의 유저 id
	 * @param pageable			한 번에 10개씩 반환하여 원하는 페이지를 선택
	 * @return	CommonResponse 성공 메세지, data: 답변, 생성일, 숨긴일
	 */
	@GetMapping("/daily-answers/hide")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<CommonResponse<Page<DailyAnswerGetHiddenResponseDto>>> getHiddenDailyAnswer(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return CommonResponse.of(SuccessCode.FOUND,
			dailyAnswerService.findHiddenDailyAnswer(currentUser.getId(), pageable));
	}

	/**
	 * 숨긴 답변 복구
	 * @param currentUser		복구할 답변의 유저 id
	 * @param dailyAnswerId		복구할 답변의 id
	 * @return	CommonResponse 성공 메세지, data: null
	 */
	@PatchMapping("/daily-answers/{dailyAnswerId}/visibility/restore")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<CommonResponse<Void>> updateDailyAnswerStatusActive(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long dailyAnswerId
	) {
		dailyAnswerService.updateDailyAnswerStatusActive(currentUser.getId(), dailyAnswerId);
		return CommonResponse.of(SuccessCode.UPDATED);
	}

	/**
	 * 답변 삭제					관리자만 가능
	 * @param dailyAnswerId		관리자가 삭제할 답변의 id
	 * @return CommonResponse 성공 메세지, data: null
	 */
	@DeleteMapping("/daily-answers/{dailyAnswerId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CommonResponse<Void>> deleteDailyAnswer(
		@PathVariable Long dailyAnswerId
	) {
		dailyAnswerService.deleteDailyAnswer(dailyAnswerId);
		return CommonResponse.of(SuccessCode.DELETED);
	}

	/**
	 * 유저의 삭제된 답변 조회	관리자만 가능
	 * @param userId			조회할 유저의 id
	 * @param pageable			한 번에 50개씩 반환하여 원하는 페이지를 선택
	 * @return	CommonResponse 성공 메세지, data: 답변, 생성일, 삭제일
	 */
	@GetMapping("/users/{userId}/daily-answers/deleted")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CommonResponse<Page<DailyAnswerGetDeletedResponse>>> getDeletedDailyAnswer(
		@PathVariable Long userId,
		@PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return CommonResponse.of(SuccessCode.FOUND, dailyAnswerService.findDeletedDailyAnswer(userId, pageable));
	}

	/**
	 * 삭제된 답변 복구			관리자만 가능
	 * @param dailyAnswerId		복구할 답변의 id
	 * @return	CommonResponse 성공 메세지, data: null
	 */
	@PatchMapping("/daily-answers/{dailyAnswerId}/restore")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CommonResponse<Void>> restoreDailyAnswer(
		@PathVariable Long dailyAnswerId
	) {
		dailyAnswerService.restoreDailyAnswer(dailyAnswerId);
		return CommonResponse.of(SuccessCode.UPDATED);
	}
}
