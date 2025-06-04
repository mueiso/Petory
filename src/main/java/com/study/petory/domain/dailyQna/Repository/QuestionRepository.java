package com.study.petory.domain.dailyQna.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.study.petory.domain.dailyQna.dto.response.QuestionGetAllResponseDto;
import com.study.petory.domain.dailyQna.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {

	boolean existsByDate(String date);

	@Query("SELECT new com.study.petory.domain.dailyQna.dto.response.QuestionGetAllResponseDto(q.question, q.date) FROM Question q")
	Page<QuestionGetAllResponseDto> findQuestionByPage(Pageable pageable);
}
