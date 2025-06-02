package com.study.petory.domain.dailyQna.service;

import org.springframework.stereotype.Service;

import com.study.petory.domain.dailyQna.Repository.QuestionRepository;
import com.study.petory.domain.dailyQna.entity.Question;
import com.study.petory.exception.CustomException;
import com.study.petory.exception.enums.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

	private final QuestionRepository questionRepository;

	@Override
	public boolean isExistQuestion(Long questionId) {
		if (!questionRepository.existsById(questionId)) {
			throw new CustomException(ErrorCode.QUESTION_NOT_FOUND);
		}
		return true;
	}

	@Override
	public Question findQuestionByQuestionIdOrElseThrow(Long questionId) {
		return questionRepository.findById(questionId)
			.orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));
	}
}
