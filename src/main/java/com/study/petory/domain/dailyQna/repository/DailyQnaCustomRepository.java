package com.study.petory.domain.dailyQna.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.study.petory.domain.dailyQna.entity.DailyQna;
import com.study.petory.domain.dailyQna.entity.DailyQnaStatus;

public interface DailyQnaCustomRepository {

	boolean isDailyQnaToday(Long userId, Long questionId);

	Optional<DailyQna> findDailyQnaByStatusAndId(List<DailyQnaStatus> statusList, Long dailyQnaId);

	List<DailyQna> findDailyQna(Long userId, Long questionId);

	Page<DailyQna> findDailyQnaPageByStatus(List<DailyQnaStatus> statusList, Long userId, Pageable pageable);

	Optional<DailyQnaStatus> findDailyQnaStatusById(Long dailyQnaId);
}
