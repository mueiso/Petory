package com.study.petory.domain.dailyQna.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.domain.dailyQna.Repository.DailyQnaRepository;
import com.study.petory.domain.dailyQna.dto.request.DailyQnaCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.request.DailyQnaUpdateRequestDto;
import com.study.petory.domain.dailyQna.dto.response.DailyQnaGetDeletedResponse;
import com.study.petory.domain.dailyQna.dto.response.DailyQnaGetHiddenResponse;
import com.study.petory.domain.dailyQna.dto.response.DailyQnaGetResponseDto;
import com.study.petory.domain.dailyQna.entity.DailyQna;
import com.study.petory.domain.dailyQna.entity.DailyQnaStatus;
import com.study.petory.domain.dailyQna.entity.Question;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;
import com.study.petory.exception.CustomException;
import com.study.petory.exception.enums.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DailyQnaServiceImpl implements DailyQnaService{

	private final DailyQnaRepository dailyQnaRepository;
	private final QuestionService questionService;

	// 리펙토링 예정
	private final UserRepository userRepository;

	// 단순 답변 조회
	@Override
	public DailyQna findDailyQnaByDailyQnaId(Long dailyQnaId) {
		DailyQna dailyQna = dailyQnaRepository.findById(dailyQnaId)
			.orElseThrow(() -> new CustomException(ErrorCode.DAILY_QNA_NOT_FOUND));
		return dailyQna;
	}

	// 상태가 정상인 답변 조회
	@Override
	public DailyQna findDailyQnaByActive(Long dailyQnaId) {
		DailyQna dailyQna = dailyQnaRepository.findDailyQnaByActive(dailyQnaId)
			.orElseThrow(() -> new CustomException(ErrorCode.DAILY_QNA_NOT_FOUND));
		return dailyQna;
	}

	// 본인의 답변이 맞는지 검증
	@Override
	public void validateAuthor(Long userId, DailyQna dailyQna) {
		if (!dailyQna.isEqualUser(userId)) {
			throw new CustomException(ErrorCode.ONLY_AUTHOR_CAN_EDIT);
		}
	}

	// 사용자의 답변을 저장
	@Override
	@Transactional
	public void saveDailyQna(Long userId, Long questionId, DailyQnaCreateRequestDto requestDto) {
		if (dailyQnaRepository.isDailyQnaToday(userId, questionId)) {
			throw new CustomException(ErrorCode.ALREADY_WRITTEN_TODAY);
		}
		// 리펙토링 예정
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		Question todayQuestion = questionService.findQuestionByQuestionId(questionId);
		dailyQnaRepository.save(DailyQna.builder()
			.user(user)
			.question(todayQuestion)
			.answer(requestDto.getAnswer())
			.dailyQnaStatus(DailyQnaStatus.ACTIVE)
			.build()
		);
	}

	// 질문에 사용자가 남긴 모든 답변 조회
	@Override
	@Transactional
	public List<DailyQnaGetResponseDto> findDailyQna(Long userId, Long questionId) {
		List<DailyQna> dailyQnaList = dailyQnaRepository.findDailyQna(userId, questionId);

		return dailyQnaList.stream()
			.map(DailyQnaGetResponseDto::from)
			.sorted(Comparator.comparing(DailyQnaGetResponseDto::getCreatedAt).reversed())
			.collect(Collectors.toList());
	}

	// 답변을 사용자가 수정
	@Override
	@Transactional
	public void updateDailyQna(Long userId, Long dailyQnaId, DailyQnaUpdateRequestDto requestDto) {
		DailyQna dailyQna = findDailyQnaByActive(dailyQnaId);
		validateAuthor(userId, dailyQna);
		dailyQna.updateDailyQna(requestDto.getAnswer());
	}

	// 답변을 숨김 처리
	@Override
	@Transactional
	public void hideDailyQna(Long userId, Long dailyQnaId) {
		DailyQna dailyQna = findDailyQnaByActive(dailyQnaId);
		validateAuthor(userId, dailyQna);
		dailyQna.updateStatusHidden();
	}

	// 숨김 처리한 답변 조회
	@Override
	public Page<DailyQnaGetHiddenResponse> findHiddenDailyQna(Long userId, Pageable pageable) {
		Page<DailyQna> dailyQnaList = dailyQnaRepository.findDailyQnaByHidden(userId, pageable);
		return dailyQnaList
			.map(DailyQnaGetHiddenResponse::from);
	}

	// 숨김 처리한 답변 정상으로 복구
	@Override
	@Transactional
	public void updateDailyQnaStatusActive(Long userId, Long dailyQnaId) {
		DailyQna dailyQna = findDailyQnaByDailyQnaId(dailyQnaId);
		validateAuthor(userId, dailyQna);
		dailyQna.updateStatusActive();
	}

	// 답변을 관리자가 삭제
	@Override
	@Transactional
	public void deleteDailyQna(Long adminId, Long dailyQnaId) {
		// 수정 예정
		// if (!userService.권한검증메서드(adminId)) {
		// 	throw new CustomException(ErrorCode.FORBIDDEN);
		// }
		DailyQna dailyQna = findDailyQnaByActive(dailyQnaId);
		dailyQna.deactivateEntity();
		dailyQna.updateStatusDelete();
	}

	// 관리자가 삭제된 답변 조회
	@Override
	public Page<DailyQnaGetDeletedResponse> findDeletedDailyQna(Long adminId, Long userId, Pageable pageable) {
		// 수정 예정
		// if (!userService.권한검증메서드(adminId)) {
		// 	throw new CustomException(ErrorCode.FORBIDDEN);
		// }
		Page<DailyQna> dailyQnaList = dailyQnaRepository.findDailyQnaByDeleted(userId, pageable);

		return dailyQnaList
			.map(DailyQnaGetDeletedResponse::from);
	}

	// 관리자가 삭제된 답변 복구
	@Override
	@Transactional
	public void restoreDailyQna(Long adminId, Long dailyQnaId) {
		// 수정 예정
		// if (!userService.권한검증메서드(adminId)) {
		// 	throw new CustomException(ErrorCode.FORBIDDEN);
		// }
		DailyQna dailyQna = findDailyQnaByDailyQnaId(dailyQnaId);
		dailyQna.updateStatusActive();
		dailyQna.restoreEntity();
	}
}
