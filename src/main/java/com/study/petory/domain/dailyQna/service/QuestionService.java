package com.study.petory.domain.dailyQna.service;

import org.springframework.data.domain.Page;

import com.study.petory.domain.dailyQna.dto.request.QuestionCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.request.QuestionUpdateRequestDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetAllResponseDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetOneResponseDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetTodayResponseDto;
import com.study.petory.domain.dailyQna.entity.Question;

public interface QuestionService {

	void setQuestion();

	Question findQuestionByQuestionId(Long questionId);

	boolean existsByDate(String date);

	void saveQuestion(Long userId, QuestionCreateRequestDto request);

	Page<QuestionGetAllResponseDto> findAllQuestion(Long userId, int page);

	QuestionGetOneResponseDto findOneQuestion(Long userId, Long questionId);

	QuestionGetTodayResponseDto findTodayQuestion();

	void updateQuestion(Long userId, Long questionId, QuestionUpdateRequestDto request);

	void deactivateQuestion(Long userId, Long questionId);

	void restoreQuestion(Long userId, Long questionId);
}
