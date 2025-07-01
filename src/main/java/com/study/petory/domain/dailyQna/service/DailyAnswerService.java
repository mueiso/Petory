package com.study.petory.domain.dailyQna.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.study.petory.domain.dailyQna.dto.request.DailyAnswerCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.request.DailyAnswerUpdateRequestDto;
import com.study.petory.domain.dailyQna.dto.response.DailyAnswerGetDeletedResponse;
import com.study.petory.domain.dailyQna.dto.response.DailyAnswerGetHiddenResponseDto;
import com.study.petory.domain.dailyQna.dto.response.DailyAnswerGetResponseDto;
import com.study.petory.domain.dailyQna.entity.DailyAnswer;
import com.study.petory.domain.dailyQna.entity.DailyAnswerStatus;

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
