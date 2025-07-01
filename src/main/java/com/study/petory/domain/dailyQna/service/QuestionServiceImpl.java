package com.study.petory.domain.dailyQna.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import com.study.petory.domain.dailyQna.dto.request.QuestionCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.request.QuestionUpdateRequestDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetAllResponseDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetDeletedResponseDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetInactiveResponseDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetOneResponseDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetTodayResponseDto;
import com.study.petory.domain.dailyQna.entity.DailyQnaStatus;
import com.study.petory.domain.dailyQna.entity.Question;
import com.study.petory.domain.dailyQna.entity.QuestionStatus;
import com.study.petory.domain.dailyQna.repository.QuestionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

	private final QuestionRepository questionRepository;

	// 질문을 저장 admin
	@Override
	@Transactional
	public void saveQuestion(QuestionCreateRequestDto request) {
		existsByDate(request.getDate());

		questionRepository.save(Question.builder()
			.content(request.getContent())
			.date(request.getDate())
			.questionStatus(QuestionStatus.ACTIVE)
			.build()
		);
	}

	// 질문 전체 조회 admin
	@Override
	public Page<QuestionGetAllResponseDto> findAllQuestion(Pageable pageable) {
		List<QuestionStatus> statusList = new ArrayList<>(List.of(QuestionStatus.ACTIVE, QuestionStatus.INACTIVE));
		Page<Question> questionPage = questionRepository.findQuestionPageByStatus(statusList, pageable);
		return questionPage.map(QuestionGetAllResponseDto::from);
	}

	// 질문 단건 조회 admin
	@Override
	public QuestionGetOneResponseDto findOneQuestion(Long questionId) {
		List<QuestionStatus> activeAndInactiveStatus = List.of(QuestionStatus.ACTIVE, QuestionStatus.INACTIVE);

		Question question = findQuestionByIdAndStatus(activeAndInactiveStatus, questionId);
		return QuestionGetOneResponseDto.from(question);
	}

	// 오늘의 질문 조회
	@Override
	@Cacheable(value = "todayQuestion")
	public QuestionGetTodayResponseDto findTodayQuestion() {
		String today = CustomDateUtil.getFormatDate();
		Question question = questionRepository.findTodayQuestion(today)
			.orElseThrow(() -> new CustomException(ErrorCode.TODAY_QUESTION_IS_DEACTIVATED));
		return QuestionGetTodayResponseDto.from(question);
	}

	// 질문 수정 admin
	@Override
	@Transactional
	@CacheEvict(value = "todayQuestion", allEntries = true)
	public void updateQuestion(Long questionId, QuestionUpdateRequestDto request) {
		List<QuestionStatus> activeAndInactiveStatus = List.of(QuestionStatus.ACTIVE, QuestionStatus.INACTIVE);

		Question question = findQuestionByIdAndStatus(activeAndInactiveStatus, questionId);

		if (questionRepository.existsByDate(request.getDate()) && !question.getDate().equals(request.getDate())) {
			throw new CustomException(ErrorCode.DATE_IS_EXIST);
		}

		question.update(request.getContent(), request.getDate());
	}

	// 질문 비활성화 admin
	@Override
	@Transactional
	@CacheEvict(value = "todayQuestion", allEntries = true)
	public void inactiveQuestion(Long questionId) {
		if (findQuestionStatusById(questionId).equals(QuestionStatus.INACTIVE)) {
			throw new CustomException(ErrorCode.QUESTION_IS_DEACTIVATED);
		}
		List<QuestionStatus> activeStatus = List.of(QuestionStatus.ACTIVE);

		Question question = findQuestionByIdAndStatus(activeStatus, questionId);
		question.updateStatusInactive();
	}

	// 비활성화 된 질문 조회 admin
	@Override
	public Page<QuestionGetInactiveResponseDto> findInactiveQuestion(Pageable pageable) {
		List<QuestionStatus> inactiveStatus = List.of(QuestionStatus.INACTIVE);

		Page<Question> questionPage = questionRepository.findQuestionPageByStatus(inactiveStatus, pageable);
		return questionPage
			.map(QuestionGetInactiveResponseDto::from);
	}

	// 비활성화 된 질문 복구 admin
	@Override
	@Transactional
	public void updateQuestionStatusActive(Long questionId) {
		if (findQuestionStatusById(questionId).equals(QuestionStatus.ACTIVE)) {
			throw new CustomException(ErrorCode.QUESTION_IS_NOT_DEACTIVATED);
		}
		List<QuestionStatus> inactiveStatus = List.of(QuestionStatus.INACTIVE);

		Question question = findQuestionByIdAndStatus(inactiveStatus, questionId);
		question.updateStatusActive();
	}

	// 질문 삭제 admin
	@Override
	@Transactional
	public void deactivateQuestion(Long questionId) {
		if (findQuestionStatusById(questionId).equals(QuestionStatus.DELETED)) {
			throw new CustomException(ErrorCode.QUESTION_IS_DELETED);
		}
		List<QuestionStatus> activeAndInactiveStatus = List.of(QuestionStatus.ACTIVE, QuestionStatus.INACTIVE);

		Question question = findQuestionByIdAndStatus(activeAndInactiveStatus, questionId);

		question.deactivateEntity();
		question.updateStatusDelete();
	}

	// 삭제된 질문 복구 admin
	@Override
	@Transactional
	public void restoreQuestion(Long questionId) {
		if (!findQuestionStatusById(questionId).equals(QuestionStatus.DELETED)) {
			throw new CustomException(ErrorCode.QUESTION_IS_NOT_DELETED);
		}
		List<QuestionStatus> deleteStatus = List.of(QuestionStatus.DELETED);

		Question question = findQuestionByIdAndStatus(deleteStatus, questionId);

		question.restoreEntity();
		question.updateStatusActive();
	}

	// 관리자가 삭제된 질문 조회 admin
	@Override
	public Page<QuestionGetDeletedResponseDto> findQuestionByDeleted(Pageable pageable) {
		List<QuestionStatus> deleteStatus = List.of(QuestionStatus.DELETED);

		Page<Question> questionList = questionRepository.findQuestionPageByStatus(deleteStatus, pageable);
		return questionList.map(QuestionGetDeletedResponseDto::from);
	}

	/*
	테스트 코드 전용
	todo 삭제 예정
	 */
	@Override
	public void setQuestion() {
		LocalDate date = LocalDate.of(2024, 1, 1);
		for (int i = 1; i <= 366; i++) {
			String monthDay = date.format(DateTimeFormatter.ofPattern("MM-dd"));
			Question question = new Question(
				"질문 " + i,
				monthDay,
				QuestionStatus.ACTIVE
			);
			date = date.plusDays(1);
			questionRepository.save(question);
		}
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
	public Question findQuestionByIdAndStatus(List<QuestionStatus> statusList, Long questionId) {
		return questionRepository.findQuestionByStatusAndId(statusList, questionId)
			.orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));
	}

	public QuestionStatus findQuestionStatusById(Long questionId) {
		return questionRepository.findQuestionStatusById(questionId)
			.orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));
	}
}
