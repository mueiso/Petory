package com.study.petory.domain.dailyqna.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.study.petory.domain.dailyqna.dto.request.DailyAnswerCreateRequestDto;
import com.study.petory.domain.dailyqna.dto.request.DailyAnswerUpdateRequestDto;
import com.study.petory.domain.dailyqna.dto.response.DailyAnswerGetDeletedResponse;
import com.study.petory.domain.dailyqna.dto.response.DailyAnswerGetHiddenResponseDto;
import com.study.petory.domain.dailyqna.dto.response.DailyAnswerGetResponseDto;
import com.study.petory.domain.dailyqna.entity.DailyAnswer;
import com.study.petory.domain.dailyqna.entity.DailyAnswerStatus;

public interface DailyAnswerService {

	DailyAnswer findDailyAnswerByStatusAndId(List<DailyAnswerStatus> statusList, Long dailyAnswerId);

	DailyAnswerStatus findDailyAnswerStatusById(Long dailyAnswerId);

	void validateAuthor(Long userId, DailyAnswer dailyAnswer);

	void saveDailyAnswer(Long userId, Long dailyQuestionId, DailyAnswerCreateRequestDto requestDto);

	List<DailyAnswerGetResponseDto> findDailyAnswer(Long userId, Long dailyQuestionId);

	void updateDailyAnswer(Long userId, Long dailyAnswerId, DailyAnswerUpdateRequestDto requestDto);

	void hideDailyAnswer(Long userId, Long dailyAnswerId);

	Page<DailyAnswerGetHiddenResponseDto> findHiddenDailyAnswer(Long userId, Pageable pageable);

	void updateDailyAnswerStatusActive(Long userId, Long dailyAnswerId);

	void deleteDailyAnswer(Long dailyAnswerId);

	Page<DailyAnswerGetDeletedResponse> findDeletedDailyAnswer(Long userId, Pageable pageable);

	void restoreDailyAnswer(Long dailyAnswerId);
}
