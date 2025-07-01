package com.study.petory.domain.dailyQna.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.domain.dailyQna.dto.request.DailyAnswerCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.request.DailyAnswerUpdateRequestDto;
import com.study.petory.domain.dailyQna.dto.response.DailyAnswerGetDeletedResponse;
import com.study.petory.domain.dailyQna.dto.response.DailyAnswerGetHiddenResponseDto;
import com.study.petory.domain.dailyQna.dto.response.DailyAnswerGetResponseDto;
import com.study.petory.domain.dailyQna.entity.DailyAnswer;
import com.study.petory.domain.dailyQna.entity.DailyAnswerStatus;
import com.study.petory.domain.dailyQna.entity.DailyQuestion;
import com.study.petory.domain.dailyQna.entity.DailyQuestionStatus;
import com.study.petory.domain.dailyQna.repository.DailyAnswerRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DailyAnswerServiceImpl implements DailyAnswerService {

	private final DailyAnswerRepository dailyAnswerRepository;
	private final DailyQuestionService dailyQuestionService;
	private final UserService userService;

	// 사용자의 답변을 저장
	@Override
	@Transactional
	public void saveDailyAnswer(Long userId, Long dailyQuestionId, DailyAnswerCreateRequestDto requestDto) {
		if (dailyAnswerRepository.isDailyAnswerToday(userId, dailyQuestionId)) {
			throw new CustomException(ErrorCode.ALREADY_WRITTEN_TODAY);
		}

		User user = userService.findUserById(userId);
		DailyQuestion todayDailyQuestion = dailyQuestionService.findDailyQuestionByIdAndStatus(
			List.of(DailyQuestionStatus.ACTIVE), dailyQuestionId);

		DailyAnswer dailyAnswer = DailyAnswer.builder()
			.dailyQuestion(todayDailyQuestion)
			.answer(requestDto.getAnswer())
			.dailyAnswerStatus(DailyAnswerStatus.ACTIVE)
			.build();

		user.addDailyQna(dailyAnswer);

		dailyAnswerRepository.save(dailyAnswer);
	}

	// 질문에 사용자가 남긴 모든 답변 조회
	@Override
	@Transactional
	public List<DailyAnswerGetResponseDto> findDailyAnswer(Long userId, Long dailyQuestionId) {
		List<DailyAnswer> dailyAnswerList = dailyAnswerRepository.findDailyAnswer(userId, dailyQuestionId);

		return dailyAnswerList.stream()
			.map(DailyAnswerGetResponseDto::from)
			.collect(Collectors.toList());
	}

	// 답변을 사용자가 수정
	@Override
	@Transactional
	public void updateDailyAnswer(Long userId, Long dailyAnswerId, DailyAnswerUpdateRequestDto requestDto) {
		List<DailyAnswerStatus> activeStatus = List.of(DailyAnswerStatus.ACTIVE);

		DailyAnswer dailyAnswer = findDailyAnswerByStatusAndId(activeStatus, dailyAnswerId);
		validateAuthor(userId, dailyAnswer);
		dailyAnswer.updateDailyQna(requestDto.getAnswer());
	}

	// 답변을 숨김 처리
	@Override
	@Transactional
	public void hideDailyAnswer(Long userId, Long dailyAnswerId) {
		if (findDailyAnswerStatusById(dailyAnswerId).equals(DailyAnswerStatus.HIDDEN)) {
			throw new CustomException(ErrorCode.DAILY_QNA_IS_HIDDEN);
		}
		List<DailyAnswerStatus> activeStatus = List.of(DailyAnswerStatus.ACTIVE);

		DailyAnswer dailyAnswer = findDailyAnswerByStatusAndId(activeStatus, dailyAnswerId);
		validateAuthor(userId, dailyAnswer);
		dailyAnswer.updateStatusHidden();
	}

	// 숨김 처리한 답변 조회
	@Override
	public Page<DailyAnswerGetHiddenResponseDto> findHiddenDailyAnswer(Long userId, Pageable pageable) {
		Page<DailyAnswer> dailyQnaList = dailyAnswerRepository.findDailyAnswerPageByStatus(List.of(DailyAnswerStatus.HIDDEN),
			userId, pageable);
		return dailyQnaList
			.map(DailyAnswerGetHiddenResponseDto::from);
	}

	// 숨김 처리한 답변 정상으로 복구
	@Override
	@Transactional
	public void updateDailyAnswerStatusActive(Long userId, Long dailyAnswerId) {
		if (!findDailyAnswerStatusById(dailyAnswerId).equals(DailyAnswerStatus.HIDDEN)) {
			throw new CustomException(ErrorCode.DAILY_QNA_IS_NOT_HIDDEN);
		}

		List<DailyAnswerStatus> hiddenStatus = List.of(DailyAnswerStatus.HIDDEN);

		DailyAnswer dailyAnswer = findDailyAnswerByStatusAndId(hiddenStatus, dailyAnswerId);
		validateAuthor(userId, dailyAnswer);
		dailyAnswer.updateStatusActive();
	}

	// 답변을 관리자가 삭제
	@Override
	@Transactional
	public void deleteDailyAnswer(Long dailyAnswerId) {
		if (findDailyAnswerStatusById(dailyAnswerId).equals(DailyAnswerStatus.DELETED)) {
			throw new CustomException(ErrorCode.DAILY_QNA_IS_DELETED);
		}

		List<DailyAnswerStatus> activeAndHiddenStatus = List.of(DailyAnswerStatus.ACTIVE, DailyAnswerStatus.HIDDEN);

		DailyAnswer dailyAnswer = findDailyAnswerByStatusAndId(activeAndHiddenStatus, dailyAnswerId);
		dailyAnswer.deactivateEntity();
		dailyAnswer.updateStatusDelete();
	}

	// 관리자가 삭제된 답변 조회
	@Override
	public Page<DailyAnswerGetDeletedResponse> findDeletedDailyAnswer(Long userId, Pageable pageable) {
		Page<DailyAnswer> dailyQnaList = dailyAnswerRepository.findDailyAnswerPageByStatus(List.of(DailyAnswerStatus.DELETED),
			userId, pageable);
		return dailyQnaList
			.map(DailyAnswerGetDeletedResponse::from);
	}

	// 관리자가 삭제된 답변 복구
	@Override
	@Transactional
	public void restoreDailyAnswer(Long dailyAnswerId) {
		if (!findDailyAnswerStatusById(dailyAnswerId).equals(DailyAnswerStatus.DELETED)) {
			throw new CustomException(ErrorCode.DAILY_QNA_IS_NOT_DELETED);
		}

		List<DailyAnswerStatus> deleteStatus = List.of(DailyAnswerStatus.DELETED);

		DailyAnswer dailyAnswer = findDailyAnswerByStatusAndId(deleteStatus, dailyAnswerId);
		dailyAnswer.updateStatusActive();
		dailyAnswer.restoreEntity();
	}

	// 상태가 정상인 답변 조회
	@Override
	public DailyAnswer findDailyAnswerByStatusAndId(List<DailyAnswerStatus> statusList, Long dailyAnswerId) {
		return dailyAnswerRepository.findDailyAnswerByStatusAndId(statusList, dailyAnswerId)
			.orElseThrow(() -> new CustomException(ErrorCode.DAILY_QNA_NOT_FOUND));
	}

	// 본인의 답변이 맞는지 검증
	@Override
	public void validateAuthor(Long userId, DailyAnswer dailyAnswer) {
		if (!dailyAnswer.isEqualUser(userId)) {
			throw new CustomException(ErrorCode.ONLY_AUTHOR_CAN_EDIT);
		}
	}

	// 답변의 상태 조회
	@Override
	public DailyAnswerStatus findDailyAnswerStatusById(Long dailyAnswerId) {
		return dailyAnswerRepository.findDailyAnswerStatusById(dailyAnswerId)
			.orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));
	}
}
