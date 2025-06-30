package com.study.petory.domain.event.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.exception.enums.SuccessCode;
import com.study.petory.common.response.CommonResponse;
import com.study.petory.common.security.CustomPrincipal;
import com.study.petory.domain.event.dto.request.EventCreateRequestDto;
import com.study.petory.domain.event.dto.request.EventUpdateRequestDto;
import com.study.petory.domain.event.dto.response.EventCreateResponseDto;
import com.study.petory.domain.event.dto.response.EventInstanceGetResponseDto;
import com.study.petory.domain.event.dto.response.EventGetOneResponseDto;
import com.study.petory.domain.event.dto.response.EventUpdateResponseDto;
import com.study.petory.domain.event.service.EventService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/calendars")
@RequiredArgsConstructor
public class EventController {

	private final EventService eventService;

	/**
	 * 일정 저장
	 * @param currenUser		일정을 생성한 유저
	 * @param request			일정에 대한 데이터
	 * @return	CommonResponse 성공 메세지, data: id, 제목, 시작일, 종료일, 하루 종일 여부, 반복 조건, 메모, 일정 색상
	 */
	@PostMapping()
	public ResponseEntity<CommonResponse<EventCreateResponseDto>> saveEvent(
		@AuthenticationPrincipal CustomPrincipal currenUser,
		@RequestBody EventCreateRequestDto request
	) {
		return CommonResponse.of(SuccessCode.CREATED, eventService.saveEvent(currenUser.getId(), request));
	}

	/**
	 * 일정 단일 조회
	 * @param eventId			일정을 조회한 유저
	 * @return	CommonResponse 성공 메세지, data: id, 제목, 시작일, 종료일, 하루 종일 여부, 반복 조건, 메모, 일정 색상
	 */
	@GetMapping("/{eventId}")
	public ResponseEntity<CommonResponse<EventGetOneResponseDto>> getEvent(
		@PathVariable Long eventId
	) {
		return CommonResponse.of(SuccessCode.FOUND, eventService.findOneEvent(eventId));
	}

	/**
	 * 일정 범위 조회
	 * @param currenUser		일정을 조회한 유저
	 * @param start				조회 범위 시작일
	 * @param end				조회 범위 종료일
	 * @return	CommonResponse 성공 메세지, data: List [일정 인스턴스, 일정 인스턴스, ...]
	 * 일정 인스턴스: id, 제목, 시작일, 종료일, 하루 종일 여부, 일정 색상
	 */
	@GetMapping()
	public ResponseEntity<CommonResponse<List<EventInstanceGetResponseDto>>> getEvents(
		@AuthenticationPrincipal CustomPrincipal currenUser,
		@RequestParam String start,
		@RequestParam String end
	) {
		return CommonResponse.of(SuccessCode.FOUND, eventService.findEvents(currenUser.getId(), start, end));
	}

	/**
	 * 일정 단일 수정
	 * @param currenUser		일정을 수정한 유저
	 * @param eventId			수정할 일정 id
	 * @param request			일정을 수정할 데이터
	 * @return	CommonResponse 성공 메세지, data: id, 제목, 시작일, 종료일, 하루 종일 여부, 반복 조건, 메모, 일정 색상
	 */
	@PutMapping("/{eventId}")
	public ResponseEntity<CommonResponse<EventUpdateResponseDto>> updateEvent(
		@AuthenticationPrincipal CustomPrincipal currenUser,
		@PathVariable Long eventId,
		@RequestBody EventUpdateRequestDto request
	) {
		return CommonResponse.of(SuccessCode.UPDATED, eventService.updateEvent(currenUser.getId(), eventId, request));
	}

	/**
	 * 일정 단일 삭제
	 * @param currenUser		일정을 삭제한 유저
	 * @param eventId			삭제할 일정 id
	 * @return	CommonResponse 성공 메세지, data: null
	 */
	@DeleteMapping("/{eventId}")
	public ResponseEntity<CommonResponse<Void>> deleteEvent(
		@AuthenticationPrincipal CustomPrincipal currenUser,
		@PathVariable Long eventId
	) {
		eventService.deleteEvent(currenUser.getId(), eventId);
		return CommonResponse.of(SuccessCode.DELETED);
	}
}
