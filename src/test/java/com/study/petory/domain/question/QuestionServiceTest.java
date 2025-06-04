package com.study.petory.domain.question;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.study.petory.domain.dailyQna.Repository.QuestionRepository;
import com.study.petory.domain.dailyQna.dto.request.QuestionCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetResponseDto;
import com.study.petory.domain.dailyQna.entity.Question;
import com.study.petory.domain.dailyQna.service.QuestionServiceImpl;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceTest {

	@InjectMocks
	private QuestionServiceImpl questionService;

	@Mock
	private QuestionRepository questionRepository;

	private Page<QuestionGetResponseDto> setQuestion(int page) {
		List<QuestionGetResponseDto> questionList = new ArrayList<>();
		LocalDate date = LocalDate.of(2024, 01, 01);
		for (int i = 1; i <= 366; i++) {
			String monthDay = date.format(DateTimeFormatter.ofPattern("MM-dd"));
			Question question = new Question(
				"질문 " + i,
				monthDay
			);
			date = date.plusDays(1);
			questionList.add(QuestionGetResponseDto.from(question));
		}

		int p = 0;
		if (page > 1) {
			p = page - 1;
		}
		PageRequest pageable = PageRequest.of(p, 50, Sort.by("date").ascending());

		int start = (int)pageable.getOffset();
		int end = Math.min(start + 50, questionList.size());
		List<QuestionGetResponseDto> pageC = questionList.subList(start, end);
		Page<QuestionGetResponseDto> testPage = new PageImpl<>(pageC, pageable, questionList.size());
		return testPage;
	}

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

	@Test
	@DisplayName("관리자가 전체 질문을 조회한다.")
	public void getAllQuestion() {
		// given
		Long userId = 1L;
		int page = 8;

		given(questionRepository.findQuestionByPage(setQuestion(page).getPageable())).willReturn(setQuestion(page));

		// when
		Page<QuestionGetResponseDto> responsePage = questionService.getAllQuestion(userId, page);

		// then
		assertThat(responsePage.getContent()).hasSize(16);
		assertThat(responsePage.getTotalElements()).isEqualTo(366);
		assertThat(responsePage.getTotalPages()).isEqualTo(8);
		assertThat(responsePage.isLast()).isTrue();
	}
}
