package com.study.petory.domain.dailyQna.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.study.petory.domain.dailyQna.dto.request.QuestionCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.request.QuestionUpdateRequestDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetAllResponseDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetDeletedResponse;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetInactiveResponse;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetOneResponseDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetTodayResponseDto;
import com.study.petory.domain.dailyQna.entity.Question;

public interface QuestionService {

	void setQuestion();

	Question findQuestionByQuestionId(Long questionId);

	Question findQuestionByActive(Long questionId);

	void existsByDate(String date);

	void saveQuestion(Long userId, QuestionCreateRequestDto request);

	Page<QuestionGetAllResponseDto> findAllQuestion(Long userId, Pageable pageable);

	QuestionGetOneResponseDto findOneQuestion(Long userId, Long questionId);

	QuestionGetTodayResponseDto findTodayQuestion();

	void updateQuestion(Long userId, Long questionId, QuestionUpdateRequestDto request);

	void InactiveQuestion(Long adminId, Long questionId);

	Page<QuestionGetInactiveResponse> findInactiveQuestion(Long adminId, Pageable pageable);

	void updateQuestionStatusActive(Long adminId, Long questionId);

	void deactivateQuestion(Long userId, Long questionId);

	Page<QuestionGetDeletedResponse> findQuestionByDeleted(Long adminId, Pageable pageable);

	void restoreQuestion(Long userId, Long questionId);
}
