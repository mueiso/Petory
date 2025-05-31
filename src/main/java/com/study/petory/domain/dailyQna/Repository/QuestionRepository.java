package com.study.petory.domain.dailyQna.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.dailyQna.entity.Question;
import com.study.petory.exception.CustomException;
import com.study.petory.exception.enums.ErrorCode;

public interface QuestionRepository extends JpaRepository<Question, Long> {

	default Question findByIdOrElseThrow(Long id) {
		return findById(id).orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));
	}
}
