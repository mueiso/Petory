package com.study.petory.domain.dailyQna.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.study.petory.domain.dailyQna.dto.request.QuestionCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.request.QuestionUpdateRequestDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetAllResponseDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetDeletedResponseDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetInactiveResponseDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetOneResponseDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetTodayResponseDto;
import com.study.petory.domain.dailyQna.entity.Question;
import com.study.petory.domain.dailyQna.entity.QuestionStatus;

public interface QuestionService {

	void saveQuestion(QuestionCreateRequestDto request);

	Page<QuestionGetAllResponseDto> findAllQuestion(Pageable pageable);

	QuestionGetOneResponseDto findOneQuestion(Long questionId);

	QuestionGetTodayResponseDto findTodayQuestion();

	void updateQuestion(Long questionId, QuestionUpdateRequestDto request);

	void inactiveQuestion(Long questionId);

	Page<QuestionGetInactiveResponseDto> findInactiveQuestion(Pageable pageable);

	void updateQuestionStatusActive(Long questionId);

	void deactivateQuestion(Long questionId);

	Page<QuestionGetDeletedResponseDto> findQuestionByDeleted(Pageable pageable);

	void restoreQuestion(Long questionId);

	void setQuestion();

	void existsByDate(String date);

	Question findQuestionByIdAndStatus(List<QuestionStatus> statusList, Long questionId);
}
