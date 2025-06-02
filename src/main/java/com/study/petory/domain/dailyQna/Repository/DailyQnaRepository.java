package com.study.petory.domain.dailyQna.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.study.petory.domain.dailyQna.dto.response.DailyQnaGetResponseDto;
import com.study.petory.domain.dailyQna.entity.DailyQna;
import com.study.petory.domain.dailyQna.entity.Question;
import com.study.petory.domain.user.entity.User;

public interface DailyQnaRepository extends JpaRepository<DailyQna, Long> {

	@Query("select new com.study.petory.domain.dailyQna.dto.response.DailyQnaGetResponseDto(d.answer, d.createdAt) "
		+ "from DailyQna d where d.user = :user and d.question = :question")
	List<DailyQnaGetResponseDto> findDailyQna(@Param("user") User user, @Param("question") Question question);
}
