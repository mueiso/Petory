package com.study.petory.domain.dailyQna.service;

import com.study.petory.domain.dailyQna.dto.request.DailyQNACreateRequestDto;

public interface DailyQnaService {

	void saveDailyQNA(Long userId, Long questionId, DailyQNACreateRequestDto requestDto);
}
