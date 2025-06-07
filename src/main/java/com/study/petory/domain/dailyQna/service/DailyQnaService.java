package com.study.petory.domain.dailyQna.service;

import java.util.List;

import com.study.petory.domain.dailyQna.dto.request.DailyQnaCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.request.DailyQnaUpdateRequestDto;
import com.study.petory.domain.dailyQna.dto.response.DailyQnaGetResponseDto;
import com.study.petory.domain.dailyQna.entity.DailyQna;

public interface DailyQnaService {

	DailyQna findDailyQnaByDailyQnaIdOrElseThrow(Long dailyQnaId);

	void saveDailyQna(Long userId, Long questionId, DailyQnaCreateRequestDto requestDto);

	List<DailyQnaGetResponseDto> findDailyQna(Long userId, Long questionId);

	void updateDailyQna(Long userId, Long questionId, Long dailyQnaId, DailyQnaUpdateRequestDto requestDto);

	void deleteDailyQna(Long userId, Long questionId, Long dailyQnaId);
}
