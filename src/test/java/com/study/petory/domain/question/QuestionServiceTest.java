// package com.study.petory.domain.question;
//
// import static org.assertj.core.api.Assertions.*;
// import static org.mockito.BDDMockito.*;
//
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;
//
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageImpl;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.Sort;
// import org.springframework.test.util.ReflectionTestUtils;
//
// import com.study.petory.domain.dailyQna.Repository.QuestionRepository;
// import com.study.petory.domain.dailyQna.dto.request.QuestionCreateRequestDto;
// import com.study.petory.domain.dailyQna.dto.request.QuestionUpdateRequestDto;
// import com.study.petory.domain.dailyQna.dto.response.QuestionGetAllResponseDto;
// import com.study.petory.domain.dailyQna.dto.response.QuestionGetOneResponseDto;
// import com.study.petory.domain.dailyQna.dto.response.QuestionGetTodayResponseDto;
// import com.study.petory.domain.dailyQna.entity.Question;
// import com.study.petory.domain.dailyQna.service.QuestionServiceImpl;
//
// @ExtendWith(MockitoExtension.class)
// public class QuestionServiceTest {
//
// 	@InjectMocks
// 	private QuestionServiceImpl questionService;
//
// 	@Mock
// 	private QuestionRepository questionRepository;
//
// 	private Page<QuestionGetAllResponseDto> setQuestion(int page) {
// 		List<QuestionGetAllResponseDto> questionList = new ArrayList<>();
// 		LocalDate date = LocalDate.of(2024, 1, 1);
// 		for (int i = 1; i <= 366; i++) {
// 			String monthDay = date.format(DateTimeFormatter.ofPattern("MM-dd"));
// 			Question question = new Question(
// 				"질문 " + i,
// 				monthDay
// 			);
// 			date = date.plusDays(1);
// 			questionList.add(QuestionGetAllResponseDto.from(question));
// 		}
//
// 		int p = 0;
// 		if (page > 1) {
// 			p = page - 1;
// 		}
// 		PageRequest pageable = PageRequest.of(p, 50, Sort.by("date").ascending());
//
// 		int start = (int)pageable.getOffset();
// 		int end = Math.min(start + 50, questionList.size());
// 		List<QuestionGetAllResponseDto> pageC = questionList.subList(start, end);
// 		Page<QuestionGetAllResponseDto> testPage = new PageImpl<>(pageC, pageable, questionList.size());
// 		return testPage;
// 	}
//
// 	@Test
// 	@DisplayName("관리자가 질문을 저장한다.")
// 	public void saveQuestion() {
// 		// given
// 		QuestionCreateRequestDto request = new QuestionCreateRequestDto("질문입니다.", "01-01");
//
// 		given(questionRepository.existsByDate("01-01")).willReturn(false);
//
// 		Long userId = 1L;
//
// 		// refactor: 유저 권한 검증 추가
//
// 		// when
// 		questionService.saveQuestion(userId, request);
//
// 		// then
// 		verify(questionRepository, times(1)).save(any());
// 	}
//
// 	@Test
// 	@DisplayName("관리자가 전체 질문을 조회한다.")
// 	public void findAllQuestion() {
// 		// given
// 		Long userId = 1L;
//
// 		Pageable pageable = PageRequest.of(8, 50, Sort.by("date").ascending());
// 		given(questionRepository.findQuestionByPage(setQuestion(8).getPageable())).willReturn(setQuestion(8));
//
// 		// when
// 		Page<QuestionGetAllResponseDto> responsePage = questionService.findAllQuestion(userId, pageable);
//
// 		// then
// 		assertThat(responsePage.getContent()).hasSize(16);
// 		assertThat(responsePage.getTotalElements()).isEqualTo(366);
// 		assertThat(responsePage.getTotalPages()).isEqualTo(8);
// 		assertThat(responsePage.isLast()).isTrue();
// 	}
//
// 	@Test
// 	@DisplayName("관리자가 단건 질문을 조회한다.")
// 	public void findOneQuestion() {
// 		// given
// 		Long userId = 1L;
// 		Long questionId = 1L;
//
// 		Question question = new Question("질문 1", "01-01");
// 		ReflectionTestUtils.setField(question, "id", 1L);
// 		question.deactivateEntity();
//
// 		given(questionRepository.findById(questionId)).willReturn(Optional.of(question));
//
// 		// when
// 		QuestionGetOneResponseDto response = questionService.findOneQuestion(userId, questionId);
//
// 		// then
// 		assertThat(response.getQuestion()).isEqualTo("질문 1");
// 		assertThat(response.getDate()).isEqualTo("01-01");
// 		assertThat(response.getDeletedAt()).isNotNull();
// 	}
//
// 	@Test
// 	@DisplayName("오늘의 질문을 조회한다.")
// 	public void findTodayQuestion() {
// 		// given
// 		LocalDate date = LocalDate.now();
// 		String today = date.format(DateTimeFormatter.ofPattern("MM-dd"));
//
// 		Question question = new Question("질문", today);
//
// 		QuestionGetTodayResponseDto a = QuestionGetTodayResponseDto.from(question);
//
// 		given(questionRepository.findTodayQuestion(today)).willReturn(Optional.of(a));
//
// 		// when
// 		QuestionGetTodayResponseDto response = questionService.findTodayQuestion();
//
// 		// then
// 		assertThat(response.getQuestion()).isEqualTo("질문");
// 		assertThat(response.getDate()).isEqualTo(today);
// 	}
//
// 	@Test
// 	@DisplayName("관리자가 질문을 수정한다.")
// 	public void updateQuestion() {
// 		// given
// 		Long userId = 1L;
// 		Long questionId = 1L;
//
// 		Question question = new Question("질문", "01-01");
// 		ReflectionTestUtils.setField(question, "id", 1L);
//
// 		given(questionRepository.findById(questionId)).willReturn(Optional.of(question));
//
// 		QuestionUpdateRequestDto request = new QuestionUpdateRequestDto("수정된 질문", null);
//
// 		// when
// 		questionService.updateQuestion(userId, questionId, request);
//
// 		//then
// 		Question updatedQuestion = questionService.findQuestionByQuestionId(questionId);
//
// 		assertThat(updatedQuestion.getQuestion()).isEqualTo(request.getQuestion());
// 		assertThat(updatedQuestion.getDate()).isNotNull();
// 	}
//
// 	@Test
// 	@DisplayName("관리자가 질문을 비활성화 한다.")
// 	public void deactivateQuestion() {
// 		// given
// 		Long userId = 1L;
// 		Long questionId = 1L;
//
// 		Question question = new Question("질문", "01-01");
// 		ReflectionTestUtils.setField(question, "id", 1L);
// 		ReflectionTestUtils.setField(question, "deletedAt", null);
//
// 		given(questionRepository.findById(questionId)).willReturn(Optional.of(question));
//
// 		// when
// 		questionService.deactivateQuestion(userId, questionId);
//
// 		// then
// 		Question deactivatedQuestion = questionService.findQuestionByQuestionId(questionId);
//
// 		assertThat(deactivatedQuestion.getDeletedAt()).isNotNull();
// 	}
//
// 	@Test
// 	@DisplayName("관리자가 질문을 복구한다.")
// 	public void restoreQuestion() {
// 		// given
// 		Long userId = 1L;
// 		Long questionId = 1L;
// 		LocalDateTime deletedTime = LocalDateTime.now();
//
// 		Question question = new Question("질문", "01-01");
// 		ReflectionTestUtils.setField(question, "id", 1L);
// 		ReflectionTestUtils.setField(question, "deletedAt", deletedTime);
//
// 		given(questionRepository.findById(questionId)).willReturn(Optional.of(question));
//
// 		// when
// 		questionService.restoreQuestion(userId, questionId);
//
// 		// then
// 		Question restoredQuestion = questionService.findQuestionByQuestionId(questionId);
//
// 		assertThat(restoredQuestion.getDeletedAt()).isNull();
// 	}
// }
