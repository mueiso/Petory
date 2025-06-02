package com.study.petory.domain.question;

import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.study.petory.domain.dailyQna.Repository.QuestionRepository;
import com.study.petory.domain.dailyQna.dto.request.QuestionCreateRequestDto;
import com.study.petory.domain.dailyQna.service.QuestionServiceImpl;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceTest {

	@InjectMocks
	private QuestionServiceImpl questionService;

	@Mock
	private QuestionRepository questionRepository;

	@Test
	@DisplayName("관리자가 질문을 저장한다.")
	public void saveQuestion() {
		// given
		QuestionCreateRequestDto request = new QuestionCreateRequestDto("질문입니다.", "01-01");

		given(questionRepository.existsByDate("01-01")).willReturn(false);

		Long userId = 1L;

		// refactor: 유저 권한 검증 추가

		// when
		questionService.saveQuestion(userId, request);

		// then
		verify(questionRepository, times(1)).save(any());
	}
}
