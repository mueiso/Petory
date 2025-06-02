package com.study.petory.domain.dailyQna.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.util.EntityFetcher;
import com.study.petory.domain.dailyQna.Repository.DailyQnaRepository;
import com.study.petory.domain.dailyQna.dto.request.DailyQnaCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.request.DailyQnaUpdateRequestDto;
import com.study.petory.domain.dailyQna.dto.response.DailyQnaGetResponseDto;
import com.study.petory.domain.dailyQna.entity.DailyQna;
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

	@Override
	public DailyQna findDailyQnaByDailyQnaIdOrElseThrow(Long dailyQnaId) {
		return dailyQnaRepository.findById(dailyQnaId)
			.orElseThrow(() -> new CustomException(ErrorCode.DAILY_QNA_NOT_FOUND));
	}

	// 사용자의 답변을 저장
	@Override
	@Transactional
	public void saveDailyQna(Long userId, Long questionId, DailyQnaCreateRequestDto requestDto) {
		// 리펙토링 예정
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
		Question todayQuestion = questionService.findQuestionByQuestionIdOrElseThrow(questionId);
		dailyQnaRepository.save(new DailyQna(
			user,
			todayQuestion,
			requestDto.getAnswer())
		);
	}

	// 질문에 사용자가 남긴 모든 답변 조회
	@Override
	@Transactional
	public List<DailyQnaGetResponseDto> findDailyQna(Long userId, Long questionId) {
		questionService.isExistQuestion(questionId);
		List<DailyQna> answerList = dailyQnaRepository.findByUserId_IdAndQuestionId_Id(userId, questionId);
		List<DailyQnaGetResponseDto> responseList = answerList.stream()
			.map(answer -> new DailyQnaGetResponseDto(
				answer.getAnswer(),
				answer.getCreatedAt()
			))
			.collect(Collectors.toList());
		responseList.sort(Comparator.comparing(DailyQnaGetResponseDto::getCreatedAt).reversed());
		return responseList;
	}

	// 답변을 사용자가 수정
	@Override
	@Transactional
	public void updateDailyQna(Long userId, Long dailyQnaId, DailyQnaUpdateRequestDto requestDto) {
		DailyQna dailyQna = findDailyQnaByDailyQnaIdOrElseThrow(dailyQnaId);
		if (dailyQna.getUserId().getId()!=userId) {
			throw new CustomException(ErrorCode.ONLY_AUTHOR_CAN_EDIT);
		}
		dailyQna.updateDailyQna(requestDto.getAnswer());
	}

	// 답변을 사용자가 삭제
	@Override
	@Transactional
	public void deleteDailyQna(Long userId, Long dailyQnaId) {
		DailyQna dailyQna = findDailyQnaByDailyQnaIdOrElseThrow(dailyQnaId);
		if (dailyQna.getUserId().getId()!=userId) {
			throw new CustomException(ErrorCode.ONLY_AUTHOR_CAN_DELETE);
		}
		dailyQna.deactivateEntity();
	}
}
