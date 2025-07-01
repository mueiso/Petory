package com.study.petory.domain.dailyQna.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.common.util.CustomDateUtil;
import com.study.petory.domain.dailyQna.dto.request.DailyQuestionCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.request.DailyQuestionUpdateRequestDto;
import com.study.petory.domain.dailyQna.dto.response.DailyQuestionGetAllResponseDto;
import com.study.petory.domain.dailyQna.dto.response.DailyQuestionGetDeletedResponseDto;
import com.study.petory.domain.dailyQna.dto.response.DailyQuestionGetInactiveResponseDto;
import com.study.petory.domain.dailyQna.dto.response.DailyQuestionGetOneResponseDto;
import com.study.petory.domain.dailyQna.dto.response.DailyQuestionGetTodayResponseDto;
import com.study.petory.domain.dailyQna.entity.DailyQuestion;
import com.study.petory.domain.dailyQna.entity.DailyQuestionStatus;
import com.study.petory.domain.dailyQna.repository.DailyQuestionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DailyQuestionServiceImpl implements DailyQuestionService {

	private final DailyQuestionRepository questionRepository;

	// 질문을 저장 admin
	@Override
	@Transactional
	public void saveDailyQuestion(DailyQuestionCreateRequestDto request) {
		existsByDate(request.getDate());

		questionRepository.save(DailyQuestion.builder()
			.question(request.getQuestion())
			.date(request.getDate())
			.dailyQuestionStatus(DailyQuestionStatus.ACTIVE)
			.build()
		);
	}

	// 질문 전체 조회 admin
	@Override
	public Page<DailyQuestionGetAllResponseDto> findAllDailyQuestion(Pageable pageable) {
		List<DailyQuestionStatus> statusList = new ArrayList<>(List.of(DailyQuestionStatus.ACTIVE, DailyQuestionStatus.INACTIVE));
		Page<DailyQuestion> questionPage = questionRepository.findDailyQuestionPageByStatus(statusList, pageable);
		return questionPage.map(DailyQuestionGetAllResponseDto::from);
	}

	// 질문 단건 조회 admin
	@Override
	public DailyQuestionGetOneResponseDto findOneDailyQuestion(Long dailyQuestionId) {
		List<DailyQuestionStatus> activeAndInactiveStatus = List.of(DailyQuestionStatus.ACTIVE, DailyQuestionStatus.INACTIVE);

		DailyQuestion dailyQuestion = findDailyQuestionByIdAndStatus(activeAndInactiveStatus, dailyQuestionId);
		return DailyQuestionGetOneResponseDto.from(dailyQuestion);
	}

	// 오늘의 질문 조회
	@Override
	@Cacheable(value = "todayQuestion")
	public DailyQuestionGetTodayResponseDto findTodayDailyQuestion() {
		String today = CustomDateUtil.getFormatDate();
		DailyQuestion dailyQuestion = questionRepository.findTodayDailyQuestion(today)
			.orElseThrow(() -> new CustomException(ErrorCode.TODAY_QUESTION_IS_DEACTIVATED));
		return DailyQuestionGetTodayResponseDto.from(dailyQuestion);
	}

	// 질문 수정 admin
	@Override
	@Transactional
	@CacheEvict(value = "todayQuestion", allEntries = true)
	public void updateDailyQuestion(Long dailyQuestionId, DailyQuestionUpdateRequestDto request) {
		List<DailyQuestionStatus> activeAndInactiveStatus = List.of(DailyQuestionStatus.ACTIVE, DailyQuestionStatus.INACTIVE);

		DailyQuestion dailyQuestion = findDailyQuestionByIdAndStatus(activeAndInactiveStatus, dailyQuestionId);

		if (questionRepository.existsByDate(request.getDate()) && !dailyQuestion.getDate().equals(request.getDate())) {
			throw new CustomException(ErrorCode.DATE_IS_EXIST);
		}

		dailyQuestion.update(request.getQuestion(), request.getDate());
	}

	// 질문 비활성화 admin
	@Override
	@Transactional
	@CacheEvict(value = "todayQuestion", allEntries = true)
	public void inactiveDailyQuestion(Long dailyQuestionId) {
		if (findQuestionStatusById(dailyQuestionId).equals(DailyQuestionStatus.INACTIVE)) {
			throw new CustomException(ErrorCode.QUESTION_IS_DEACTIVATED);
		}
		List<DailyQuestionStatus> activeStatus = List.of(DailyQuestionStatus.ACTIVE);

		DailyQuestion dailyQuestion = findDailyQuestionByIdAndStatus(activeStatus, dailyQuestionId);
		dailyQuestion.updateStatusInactive();
	}

	// 비활성화 된 질문 조회 admin
	@Override
	public Page<DailyQuestionGetInactiveResponseDto> findInactiveDailyQuestion(Pageable pageable) {
		List<DailyQuestionStatus> inactiveStatus = List.of(DailyQuestionStatus.INACTIVE);

		Page<DailyQuestion> questionPage = questionRepository.findDailyQuestionPageByStatus(inactiveStatus, pageable);
		return questionPage
			.map(DailyQuestionGetInactiveResponseDto::from);
	}

	// 비활성화 된 질문 복구 admin
	@Override
	@Transactional
	public void updateDailyQuestionStatusActive(Long dailyQuestionId) {
		if (findQuestionStatusById(dailyQuestionId).equals(DailyQuestionStatus.ACTIVE)) {
			throw new CustomException(ErrorCode.QUESTION_IS_NOT_DEACTIVATED);
		}
		List<DailyQuestionStatus> inactiveStatus = List.of(DailyQuestionStatus.INACTIVE);

		DailyQuestion dailyQuestion = findDailyQuestionByIdAndStatus(inactiveStatus, dailyQuestionId);
		dailyQuestion.updateStatusActive();
	}

	// 질문 삭제 admin
	@Override
	@Transactional
	public void deactivateDailyQuestion(Long dailyQuestionId) {
		if (findQuestionStatusById(dailyQuestionId).equals(DailyQuestionStatus.DELETED)) {
			throw new CustomException(ErrorCode.QUESTION_IS_DELETED);
		}
		List<DailyQuestionStatus> activeAndInactiveStatus = List.of(DailyQuestionStatus.ACTIVE, DailyQuestionStatus.INACTIVE);

		DailyQuestion dailyQuestion = findDailyQuestionByIdAndStatus(activeAndInactiveStatus, dailyQuestionId);

		dailyQuestion.deactivateEntity();
		dailyQuestion.updateStatusDelete();
	}

	// 삭제된 질문 복구 admin
	@Override
	@Transactional
	public void restoreDailyQuestion(Long dailyQuestionId) {
		if (!findQuestionStatusById(dailyQuestionId).equals(DailyQuestionStatus.DELETED)) {
			throw new CustomException(ErrorCode.QUESTION_IS_NOT_DELETED);
		}
		List<DailyQuestionStatus> deleteStatus = List.of(DailyQuestionStatus.DELETED);

		DailyQuestion dailyQuestion = findDailyQuestionByIdAndStatus(deleteStatus, dailyQuestionId);

		dailyQuestion.restoreEntity();
		dailyQuestion.updateStatusActive();
	}

	// 관리자가 삭제된 질문 조회 admin
	@Override
	public Page<DailyQuestionGetDeletedResponseDto> findDailyQuestionByDeleted(Pageable pageable) {
		List<DailyQuestionStatus> deleteStatus = List.of(DailyQuestionStatus.DELETED);

		Page<DailyQuestion> questionList = questionRepository.findDailyQuestionPageByStatus(deleteStatus, pageable);
		return questionList.map(DailyQuestionGetDeletedResponseDto::from);
	}

	// 해당 일자에 해당하는 질문이 있는지 확인
	@Override
	public void existsByDate(String date) {
		if (questionRepository.existsByDate(date)) {
			throw new CustomException(ErrorCode.DATE_IS_EXIST);
		}
	}

	// id와 상태 코드로 조회
	@Override
	public DailyQuestion findDailyQuestionByIdAndStatus(List<DailyQuestionStatus> statusList, Long dailyQuestionId) {
		return questionRepository.findDailyQuestionByStatusAndId(statusList, dailyQuestionId)
			.orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));
	}

	public DailyQuestionStatus findQuestionStatusById(Long questionId) {
		return questionRepository.findDailyQuestionStatusById(questionId)
			.orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));
	}
}
