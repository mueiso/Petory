package com.study.petory.domain.dailyQna.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.domain.dailyQna.dto.request.DailyQnaCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.request.DailyQnaUpdateRequestDto;
import com.study.petory.domain.dailyQna.dto.response.DailyQnaGetDeletedResponse;
import com.study.petory.domain.dailyQna.dto.response.DailyQnaGetHiddenResponseDto;
import com.study.petory.domain.dailyQna.dto.response.DailyQnaGetResponseDto;
import com.study.petory.domain.dailyQna.entity.DailyQna;
import com.study.petory.domain.dailyQna.entity.DailyQnaStatus;
import com.study.petory.domain.dailyQna.entity.Question;
import com.study.petory.domain.dailyQna.entity.QuestionStatus;
import com.study.petory.domain.dailyQna.repository.DailyQnaRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DailyQnaServiceImpl implements DailyQnaService{

	private final DailyQnaRepository dailyQnaRepository;
	private final QuestionService questionService;
	private final UserService userService;

	// 사용자의 답변을 저장
	@Override
	@Transactional
	public void saveDailyQna(Long userId, Long questionId, DailyQnaCreateRequestDto requestDto) {
		if (dailyQnaRepository.isDailyQnaToday(userId, questionId)) {
			throw new CustomException(ErrorCode.ALREADY_WRITTEN_TODAY);
		}

		User user = userService.findUserById(userId);
		Question todayQuestion = questionService.findQuestionByIdAndStatus(
			List.of(QuestionStatus.ACTIVE), questionId);

		DailyQna dailyQna = DailyQna.builder()
			.question(todayQuestion)
			.answer(requestDto.getAnswer())
			.dailyQnaStatus(DailyQnaStatus.ACTIVE)
			.build();

		user.addDailyQna(dailyQna);

		dailyQnaRepository.save(dailyQna);
	}

	// 질문에 사용자가 남긴 모든 답변 조회
	@Override
	@Transactional
	public List<DailyQnaGetResponseDto> findDailyQna(Long userId, Long questionId) {
		List<DailyQna> dailyQnaList = dailyQnaRepository.findDailyQna(userId, questionId);

		return dailyQnaList.stream()
			.map(DailyQnaGetResponseDto::from)
			.collect(Collectors.toList());
	}

	// 답변을 사용자가 수정
	@Override
	@Transactional
	public void updateDailyQna(Long userId, Long dailyQnaId, DailyQnaUpdateRequestDto requestDto) {
		List<DailyQnaStatus> activeStatus = List.of(DailyQnaStatus.ACTIVE);

		DailyQna dailyQna = findDailyQnaByStatusAndId(activeStatus, dailyQnaId);
		validateAuthor(userId, dailyQna);
		dailyQna.updateDailyQna(requestDto.getAnswer());
	}

	// 답변을 숨김 처리
	@Override
	@Transactional
	public void hideDailyQna(Long userId, Long dailyQnaId) {
		if (findDailyQnaStatusById(dailyQnaId).equals(DailyQnaStatus.HIDDEN)) {
			throw new CustomException(ErrorCode.DAILY_QNA_IS_HIDDEN);
		}
		List<DailyQnaStatus> activeStatus = List.of(DailyQnaStatus.ACTIVE);

		DailyQna dailyQna = findDailyQnaByStatusAndId(activeStatus, dailyQnaId);
		validateAuthor(userId, dailyQna);
		dailyQna.updateStatusHidden();
	}

	// 숨김 처리한 답변 조회
	@Override
	public Page<DailyQnaGetHiddenResponseDto> findHiddenDailyQna(Long userId, Pageable pageable) {
		Page<DailyQna> dailyQnaList = dailyQnaRepository.findDailyQnaPageByStatus(List.of(DailyQnaStatus.HIDDEN),
			userId, pageable);
		return dailyQnaList
			.map(DailyQnaGetHiddenResponseDto::from);
	}

	// 숨김 처리한 답변 정상으로 복구
	@Override
	@Transactional
	public void updateDailyQnaStatusActive(Long userId, Long dailyQnaId) {
		if (!findDailyQnaStatusById(dailyQnaId).equals(DailyQnaStatus.HIDDEN)) {
			throw new CustomException(ErrorCode.DAILY_QNA_IS_NOT_HIDDEN);
		}

		List<DailyQnaStatus> hiddenStatus = List.of(DailyQnaStatus.HIDDEN);

		DailyQna dailyQna = findDailyQnaByStatusAndId(hiddenStatus, dailyQnaId);
		validateAuthor(userId, dailyQna);
		dailyQna.updateStatusActive();
	}

	// 답변을 관리자가 삭제
	@Override
	@Transactional
	public void deleteDailyQna(Long dailyQnaId) {
		if (findDailyQnaStatusById(dailyQnaId).equals(DailyQnaStatus.DELETED)) {
			throw new CustomException(ErrorCode.DAILY_QNA_IS_DELETED);
		}

		List<DailyQnaStatus> activeAndHiddenStatus = List.of(DailyQnaStatus.ACTIVE, DailyQnaStatus.HIDDEN);

		DailyQna dailyQna = findDailyQnaByStatusAndId(activeAndHiddenStatus, dailyQnaId);
		dailyQna.deactivateEntity();
		dailyQna.updateStatusDelete();
	}

	// 관리자가 삭제된 답변 조회
	@Override
	public Page<DailyQnaGetDeletedResponse> findDeletedDailyQna(Long userId, Pageable pageable) {
		Page<DailyQna> dailyQnaList = dailyQnaRepository.findDailyQnaPageByStatus(List.of(DailyQnaStatus.DELETED),
			userId, pageable);
		return dailyQnaList
			.map(DailyQnaGetDeletedResponse::from);
	}

	// 관리자가 삭제된 답변 복구
	@Override
	@Transactional
	public void restoreDailyQna(Long dailyQnaId) {
		if (!findDailyQnaStatusById(dailyQnaId).equals(DailyQnaStatus.DELETED)) {
			throw new CustomException(ErrorCode.DAILY_QNA_IS_NOT_DELETED);
		}

		List<DailyQnaStatus> deleteStatus = List.of(DailyQnaStatus.DELETED);

		DailyQna dailyQna = findDailyQnaByStatusAndId(deleteStatus, dailyQnaId);
		dailyQna.updateStatusActive();
		dailyQna.restoreEntity();
	}

	// 상태가 정상인 답변 조회
	@Override
	public DailyQna findDailyQnaByStatusAndId(List<DailyQnaStatus> statusList, Long dailyQnaId) {
		return dailyQnaRepository.findDailyQnaByStatusAndId(statusList, dailyQnaId)
			.orElseThrow(() -> new CustomException(ErrorCode.DAILY_QNA_NOT_FOUND));
	}

	// 본인의 답변이 맞는지 검증
	@Override
	public void validateAuthor(Long userId, DailyQna dailyQna) {
		if (!dailyQna.isEqualUser(userId)) {
			throw new CustomException(ErrorCode.ONLY_AUTHOR_CAN_EDIT);
		}
	}

	// 답변의 상태 조회
	@Override
	public DailyQnaStatus findDailyQnaStatusById(Long dailyQnaId) {
		return dailyQnaRepository.findDailyQnaStatusById(dailyQnaId)
			.orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));
	}
}
