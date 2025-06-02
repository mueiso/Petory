package com.study.petory.domain.dailyQna.service;

import com.study.petory.domain.dailyQna.entity.Question;

public interface QuestionService {

	boolean isExistQuestion(Long questionId);

	Question findQuestionByQuestionIdOrElseThrow(Long questionId);
}
