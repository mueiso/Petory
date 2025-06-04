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

	Question findQuestionByQuestionIdOrElseThrow(Long questionId);

	boolean existsByDate(String date);

	void saveQuestion(Long userId, QuestionCreateRequestDto request);

	Page<QuestionGetAllResponseDto> getAllQuestion(Long userId, int page);

	QuestionGetOneResponseDto getOneQuestion(Long userId, Long questionId);

	QuestionGetTodayResponseDto getTodayQuestion();

	void updateQuestion(Long userId, Long questionId, QuestionUpdateRequestDto request);

}
