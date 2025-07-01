package com.study.petory.domain.dailyQna.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.study.petory.domain.dailyQna.dto.request.DailyQuestionCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.request.DailyQuestionUpdateRequestDto;
import com.study.petory.domain.dailyQna.dto.response.DailyQuestionGetAllResponseDto;
import com.study.petory.domain.dailyQna.dto.response.DailyQuestionGetDeletedResponseDto;
import com.study.petory.domain.dailyQna.dto.response.DailyQuestionGetInactiveResponseDto;
import com.study.petory.domain.dailyQna.dto.response.DailyQuestionGetOneResponseDto;
import com.study.petory.domain.dailyQna.dto.response.DailyQuestionGetTodayResponseDto;
import com.study.petory.domain.dailyQna.entity.DailyQuestion;
import com.study.petory.domain.dailyQna.entity.DailyQuestionStatus;

public interface DailyQuestionService {

	void saveDailyQuestion(DailyQuestionCreateRequestDto request);

	Page<DailyQuestionGetAllResponseDto> findAllDailyQuestion(Pageable pageable);

	DailyQuestionGetOneResponseDto findOneDailyQuestion(Long dailyQuestionId);

	DailyQuestionGetTodayResponseDto findTodayDailyQuestion();

	void updateDailyQuestion(Long dailyQuestionId, DailyQuestionUpdateRequestDto request);

	void inactiveDailyQuestion(Long dailyQuestionId);

	Page<DailyQuestionGetInactiveResponseDto> findInactiveDailyQuestion(Pageable pageable);

	void updateDailyQuestionStatusActive(Long dailyQuestionId);

	void deactivateDailyQuestion(Long dailyQuestionId);

	Page<DailyQuestionGetDeletedResponseDto> findDailyQuestionByDeleted(Pageable pageable);

	void restoreDailyQuestion(Long dailyQuestionId);

	void existsByDate(String date);

	DailyQuestion findDailyQuestionByIdAndStatus(List<DailyQuestionStatus> statusList, Long dailyQuestionId);
}
