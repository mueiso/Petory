package com.study.petory.domain.calendar.controller;

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
import com.study.petory.domain.calendar.dto.request.EventCreateRequestDto;
import com.study.petory.domain.calendar.dto.request.EventUpdateRequestDto;
import com.study.petory.domain.calendar.dto.response.EventCreateResponseDto;
import com.study.petory.domain.calendar.dto.response.EventGetListResponseDto;
import com.study.petory.domain.calendar.dto.response.EventGetOneResponseDto;
import com.study.petory.domain.calendar.dto.response.EventUpdateResponseDto;
import com.study.petory.domain.calendar.service.EventService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/calendars")
@RequiredArgsConstructor
public class EventController {

	private final EventService eventService;

	// @PostMapping()
	// public ResponseEntity<CommonResponse<EventCreateResponseDto>> saveEvent(
	// 	@AuthenticationPrincipal CustomPrincipal currenUser,
	// 	@RequestBody EventCreateRequestDto request
	// ) {
	// 	return CommonResponse.of(SuccessCode.CREATED, eventService.saveEvent(currenUser.getId(), request));
	// }
	//
	// @GetMapping()
	// public ResponseEntity<CommonResponse<List<EventGetListResponseDto>>> getEvents(
	// 	@AuthenticationPrincipal CustomPrincipal currenUser,
	// 	@RequestParam String start,
	// 	@RequestParam String end
	// ) {
	// 	return CommonResponse.of(SuccessCode.FOUND, eventService.findEvents(currenUser.getId(), start, end));
	// }
	//
	// @GetMapping("/{eventId}")
	// public ResponseEntity<CommonResponse<EventGetOneResponseDto>> getEvent(
	// 	@PathVariable Long eventId
	// ) {
	// 	return CommonResponse.of(SuccessCode.FOUND, eventService.findOneEvent(eventId));
	// }
	//
	// @PutMapping("/{eventId}")
	// public ResponseEntity<CommonResponse<EventUpdateResponseDto>> updateEvent(
	// 	@AuthenticationPrincipal CustomPrincipal currenUser,
	// 	@PathVariable Long eventId,
	// 	@RequestBody EventUpdateRequestDto request
	// ) {
	// 	return CommonResponse.of(SuccessCode.UPDATED, eventService.updateEvent(currenUser.getId(), eventId, request));
	// }
	//
	// @DeleteMapping("/{eventId}")
	// public ResponseEntity<CommonResponse<Void>> deleteEvent(
	// 	@AuthenticationPrincipal CustomPrincipal currenUser,
	// 	@PathVariable Long eventId
	// ) {
	// 	eventService.deleteEvent(currenUser.getId(), eventId);
	// 	return CommonResponse.of(SuccessCode.DELETED);
	// }
}
