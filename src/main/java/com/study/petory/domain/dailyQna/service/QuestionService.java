package com.study.petory.domain.dailyQna.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.study.petory.domain.dailyQna.dto.request.QuestionCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetResponseDto;
import com.study.petory.domain.dailyQna.entity.Question;

public interface QuestionService {

	Question findQuestionByQuestionIdOrElseThrow(Long questionId);

	boolean existsByDate(String date);

	void saveQuestion(Long userId, QuestionCreateRequestDto request);

	Page<QuestionGetResponseDto> getAllQuestion(Long userId, Pageable pageable);
}
