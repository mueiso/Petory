package com.study.petory.domain.dailyQna.service;

import com.study.petory.domain.dailyQna.entity.Question;

public interface QuestionService {

	Question findQuestionByQuestionIdOrElseThrow(Long questionId);
}
