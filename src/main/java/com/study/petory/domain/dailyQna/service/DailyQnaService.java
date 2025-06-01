package com.study.petory.domain.dailyQna.service;

import java.util.List;

import com.study.petory.domain.dailyQna.dto.request.DailyQnaCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.response.DailyQnaGetResponseDto;

public interface DailyQnaService {

	void saveDailyQNA(Long userId, Long questionId, DailyQnaCreateRequestDto requestDto);

	List<DailyQnaGetResponseDto> findDailyQna(Long userId, Long questionId);
}
