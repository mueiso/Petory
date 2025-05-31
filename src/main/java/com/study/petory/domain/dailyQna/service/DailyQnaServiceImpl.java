package com.study.petory.domain.dailyQna.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.util.EntityFetcher;
import com.study.petory.domain.dailyQna.Repository.DailyQnaRepository;
import com.study.petory.domain.dailyQna.dto.request.DailyQNACreateRequestDto;
import com.study.petory.domain.dailyQna.entity.DailyQna;
import com.study.petory.domain.dailyQna.entity.Question;
import com.study.petory.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DailyQnaServiceImpl implements DailyQnaService{

	private final DailyQnaRepository dailyQnaRepository;
	private final EntityFetcher entityFetcher;

	// 사용자의 답변을 저장하는 메서드
	@Override
	@Transactional
	public void saveDailyQNA(Long userId, Long questionId, DailyQNACreateRequestDto requestDto) {
		User user = entityFetcher.findUserByUserId(userId);
		Question todayQuestion = entityFetcher.findQuestionByQuestionId(questionId);
		dailyQnaRepository.save(new DailyQna(
			user,
			todayQuestion,
			requestDto.getAnswer())
		);
	}
}
