package com.study.petory.domain.notification.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.exception.enums.SuccessCode;
import com.study.petory.common.response.CommonResponse;
import com.study.petory.common.security.CustomPrincipal;
import com.study.petory.domain.notification.dto.response.NotificationGetResponseDto;
import com.study.petory.domain.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<CommonResponse<Page<NotificationGetResponseDto>>> getNotificationByUser(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return CommonResponse.of(SuccessCode.FOUND, notificationService.findNotificationByUser(currentUser.getId(), pageable));
	}

	@DeleteMapping("/{notificationId}")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<CommonResponse<Void>> deleteNotification(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long notificationId
	) {
		notificationService.deleteNotification(currentUser.getId(), notificationId);
		return CommonResponse.of(SuccessCode.DELETED);
	}
}
