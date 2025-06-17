package com.study.petory.domain.dailyQna.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.study.petory.domain.dailyQna.entity.Question;
import com.study.petory.domain.dailyQna.entity.QuestionStatus;

public interface QuestionCustomRepository {

	boolean existsByDate(String date);

	Optional<Question> findQuestionByStatusAndId(List<QuestionStatus> statusList, Long questionId);

	Page<Question> findQuestionPageByStatus(List<QuestionStatus> statusList, Pageable pageable);

	Optional<Question> findTodayQuestion(String date);

	Optional<QuestionStatus> findQuestionStatusById(Long questionId);
}
