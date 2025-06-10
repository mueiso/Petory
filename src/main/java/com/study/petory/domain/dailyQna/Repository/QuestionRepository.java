package com.study.petory.domain.dailyQna.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.study.petory.domain.dailyQna.dto.response.QuestionGetAllResponseDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetTodayResponseDto;
import com.study.petory.domain.dailyQna.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {

	boolean existsByDate(String date);

	@Query("SELECT new com.study.petory.domain.dailyQna.dto.response.QuestionGetAllResponseDto(q.question, q.date, q.deletedAt) FROM Question q")
	Page<QuestionGetAllResponseDto> findQuestionByPage(Pageable pageable);

	@Query("SELECT new com.study.petory.domain.dailyQna.dto.response.QuestionGetTodayResponseDto(q.question, q.date) FROM Question q WHERE q.date LIKE :date AND q.deletedAt IS NULL")
	Optional<QuestionGetTodayResponseDto> findTodayQuestion(@Param("date") String date);
}
