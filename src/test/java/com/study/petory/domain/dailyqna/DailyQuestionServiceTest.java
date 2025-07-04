package com.study.petory.domain.dailyqna;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import com.study.petory.domain.dailyqna.dto.request.DailyQuestionCreateRequestDto;
import com.study.petory.domain.dailyqna.dto.request.DailyQuestionUpdateRequestDto;
import com.study.petory.domain.dailyqna.dto.response.DailyQuestionGetAllResponseDto;
import com.study.petory.domain.dailyqna.dto.response.DailyQuestionGetDeletedResponseDto;
import com.study.petory.domain.dailyqna.dto.response.DailyQuestionGetInactiveResponseDto;
import com.study.petory.domain.dailyqna.dto.response.DailyQuestionGetOneResponseDto;
import com.study.petory.domain.dailyqna.dto.response.DailyQuestionGetTodayResponseDto;
import com.study.petory.domain.dailyqna.entity.DailyQuestion;
import com.study.petory.domain.dailyqna.entity.DailyQuestionStatus;
import com.study.petory.domain.dailyqna.repository.DailyQuestionRepository;
import com.study.petory.domain.dailyqna.service.DailyQuestionServiceImpl;

@ExtendWith(MockitoExtension.class)
public class DailyQuestionServiceTest {

	@InjectMocks
	private DailyQuestionServiceImpl questionService;

	@Mock
	private DailyQuestionRepository questionRepository;

	private Page<DailyQuestion> setQuestion(int total, DailyQuestionStatus dailyQuestionStatus, Pageable pageable) {

		List<DailyQuestion> dailyQuestionList = new ArrayList<>();
		LocalDate date = LocalDate.of(2024, 1, 1);
		for (int i = 1; i <= total; i++) {
			String monthDay = date.format(DateTimeFormatter.ofPattern("MM-dd"));
			DailyQuestion dailyQuestion = new DailyQuestion(
				"질문 " + i,
				monthDay,
				dailyQuestionStatus
			);
			date = date.plusDays(1);
			if (dailyQuestionStatus == DailyQuestionStatus.DELETED) {
				dailyQuestion.deactivateEntity();
			}
			dailyQuestionList.add(dailyQuestion);
		}

		int start = (int)pageable.getOffset();
		int end = Math.min(start + pageable.getPageSize(), dailyQuestionList.size());
		List<DailyQuestion> pageC = dailyQuestionList.subList(start, end);
		Page<DailyQuestion> testPage = new PageImpl<>(pageC, pageable, dailyQuestionList.size());
		return testPage;
	}

	@Test
	@DisplayName("관리자가 질문을 저장한다.")
	public void saveQuestion() {
		// given
		DailyQuestionCreateRequestDto request = new DailyQuestionCreateRequestDto("질문입니다.", "01-01");

		given(questionRepository.existsByDate("01-01")).willReturn(false);

		// when
		questionService.saveDailyQuestion(request);

		// then
		verify(questionRepository, times(1)).save(any());
	}

	@Test
	@DisplayName("관리자가 전체 질문을 조회한다.")
	public void findAllQuestion() {
		// given
		Pageable pageable = PageRequest.of(7, 50, Sort.by("date").ascending());
		given(questionRepository.findDailyQuestionPageByStatus(List.of(DailyQuestionStatus.ACTIVE, DailyQuestionStatus.INACTIVE),
			pageable))
			.willReturn(setQuestion(366, DailyQuestionStatus.ACTIVE, pageable));

		// when
		Page<DailyQuestionGetAllResponseDto> responsePage = questionService.findAllDailyQuestion(pageable);

		// then
		assertThat(responsePage.getContent()).hasSize(16);
		assertThat(responsePage.getTotalElements()).isEqualTo(366);
		assertThat(responsePage.getTotalPages()).isEqualTo(8);
		assertThat(responsePage.isLast()).isTrue();
	}

	@Test
	@DisplayName("관리자가 단건 질문을 조회한다.")
	public void findOneQuestion() {
		// given
		Long questionId = 1L;

		DailyQuestion dailyQuestion = new DailyQuestion("질문 1", "01-01", DailyQuestionStatus.ACTIVE);
		ReflectionTestUtils.setField(dailyQuestion, "id", 1L);
		dailyQuestion.deactivateEntity();

		given(questionRepository.findDailyQuestionByStatusAndId(List.of(DailyQuestionStatus.ACTIVE, DailyQuestionStatus.INACTIVE),
			questionId))
			.willReturn(Optional.of(dailyQuestion));

		// when
		DailyQuestionGetOneResponseDto response = questionService.findOneDailyQuestion(questionId);

		// then
		assertThat(response.getQuestion()).isEqualTo("질문 1");
		assertThat(response.getDate()).isEqualTo("01-01");
		assertThat(response.getDailyQuestionStatus()).isNull();
		assertThat(response.getUpdatedAt()).isNull();
	}

	@Test
	@DisplayName("오늘의 질문을 조회한다.")
	public void findTodayQuestion() {
		// given
		LocalDate date = LocalDate.now();
		String today = date.format(DateTimeFormatter.ofPattern("MM-dd"));

		DailyQuestion dailyQuestion = new DailyQuestion("오늘의 질문", today, DailyQuestionStatus.ACTIVE);

		given(questionRepository.findTodayDailyQuestion(today))
			.willReturn(Optional.of(dailyQuestion));

		// when
		DailyQuestionGetTodayResponseDto response = questionService.findTodayDailyQuestion();

		// then
		assertThat(response.getQuestion()).isEqualTo("오늘의 질문");
		assertThat(response.getDate()).isEqualTo(today);
	}

	@Test
	@DisplayName("관리자가 질문을 수정한다.")
	public void updateQuestion() {
		// given
		Long questionId = 1L;

		DailyQuestion dailyQuestion = new DailyQuestion("수정 전 질문", "01-01", DailyQuestionStatus.ACTIVE);
		ReflectionTestUtils.setField(dailyQuestion, "id", 1L);

		given(questionRepository.findDailyQuestionByStatusAndId(List.of(DailyQuestionStatus.ACTIVE, DailyQuestionStatus.INACTIVE),
			questionId))
			.willReturn(Optional.of(dailyQuestion));

		DailyQuestionUpdateRequestDto request = new DailyQuestionUpdateRequestDto("수정된 질문", "01-02");

		// when
		questionService.updateDailyQuestion(questionId, request);

		//then
		DailyQuestion updatedDailyQuestion = questionService.findDailyQuestionByIdAndStatus(
			List.of(DailyQuestionStatus.ACTIVE, DailyQuestionStatus.INACTIVE), questionId);

		assertThat(updatedDailyQuestion.getQuestion()).isEqualTo("수정된 질문");
		assertThat(updatedDailyQuestion.getDate()).isEqualTo("01-02");
	}

	@Test
	@DisplayName("관리자가 질문을 비활성화 한다.")
	public void InactiveQuestion() {
		// given
		Long questionId = 1L;

		DailyQuestion dailyQuestion = new DailyQuestion("비활성화 전 질문", "01-01", DailyQuestionStatus.ACTIVE);
		ReflectionTestUtils.setField(dailyQuestion, "id", 1L);

		given(questionRepository.findDailyQuestionStatusById(questionId))
			.willReturn(Optional.of(DailyQuestionStatus.ACTIVE));

		given(questionRepository.findDailyQuestionByStatusAndId(
			List.of(DailyQuestionStatus.ACTIVE),
			questionId))
			.willReturn(Optional.of(dailyQuestion)
			);

		// when
		questionService.inactiveDailyQuestion(questionId);

		// then
		assertThat(dailyQuestion.getDailyQuestionStatus()).isEqualTo(DailyQuestionStatus.INACTIVE);
	}

	@Test
	@DisplayName("관리자가 비활성화 된 질문을 조회한다.")
	public void findInactiveQuestion() {
		// given
		Pageable pageable = PageRequest.of(1, 50, Sort.by("updatedAt").ascending());

		given(questionRepository.findDailyQuestionPageByStatus(List.of(DailyQuestionStatus.INACTIVE), pageable))
			.willReturn(setQuestion(60, DailyQuestionStatus.INACTIVE, pageable));

		// when
		Page<DailyQuestionGetInactiveResponseDto> responsePage = questionService.findInactiveDailyQuestion(pageable);

		// then
		assertThat(responsePage.getContent()).hasSize(10);
		assertThat(responsePage.getTotalElements()).isEqualTo(60);
		assertThat(responsePage.getTotalPages()).isEqualTo(2);
		assertThat(responsePage.isLast()).isTrue();
	}

	@Test
	@DisplayName("관리자가 비활성화 된 질문을 복구한다.")
	public void updateQuestionStatusActive() {
		// given
		Long questionId = 1L;

		DailyQuestion dailyQuestion = new DailyQuestion("질문", "01-01", DailyQuestionStatus.INACTIVE);
		ReflectionTestUtils.setField(dailyQuestion, "id", 1L);

		given(questionRepository.findDailyQuestionByStatusAndId(List.of(DailyQuestionStatus.INACTIVE), questionId))
			.willReturn(Optional.of(dailyQuestion));
		given(questionRepository.findDailyQuestionStatusById(questionId))
			.willReturn(Optional.of(DailyQuestionStatus.INACTIVE));

		// when
		questionService.updateDailyQuestionStatusActive(questionId);

		// then
		assertThat(dailyQuestion.getDailyQuestionStatus()).isEqualTo(DailyQuestionStatus.ACTIVE);
	}

	@Test
	@DisplayName("관리자가 질문을 삭제한다.")
	public void deactivateQuestion() {
		// given
		Long questionId = 1L;

		DailyQuestion dailyQuestion = new DailyQuestion("질문", "01-01", DailyQuestionStatus.ACTIVE);
		ReflectionTestUtils.setField(dailyQuestion, "id", 1L);
		ReflectionTestUtils.setField(dailyQuestion, "deletedAt", null);

		given(questionRepository.findDailyQuestionByStatusAndId(List.of(DailyQuestionStatus.ACTIVE, DailyQuestionStatus.INACTIVE),
			questionId))
			.willReturn(Optional.of(dailyQuestion));
		given(questionRepository.findDailyQuestionStatusById(questionId))
			.willReturn(Optional.of(DailyQuestionStatus.ACTIVE));

		// when
		questionService.deactivateDailyQuestion(questionId);

		// then
		assertThat(dailyQuestion.getDailyQuestionStatus()).isEqualTo(DailyQuestionStatus.DELETED);
		assertThat(dailyQuestion.getDeletedAt()).isNotNull();
	}

	@Test
	@DisplayName("관리자가 삭제된 질문을 조회한다.")
	public void findQuestionByDeleted() {
		// given
		Pageable pageable = PageRequest.of(1, 50, Sort.by("deletedAt").ascending());

		given(questionRepository.findDailyQuestionPageByStatus(List.of(DailyQuestionStatus.DELETED), pageable))
			.willReturn(setQuestion(60, DailyQuestionStatus.DELETED, pageable));

		// when
		Page<DailyQuestionGetDeletedResponseDto> responsePage = questionService.findDailyQuestionByDeleted(pageable);

		// then
		assertThat(responsePage.getContent()).hasSize(10);
		assertThat(responsePage.getTotalElements()).isEqualTo(60);
		assertThat(responsePage.getTotalPages()).isEqualTo(2);
		assertThat(responsePage.isLast()).isTrue();
	}

	@Test
	@DisplayName("관리자가 삭제된 질문을 복구한다.")
	public void restoreQuestion() {
		// given
		Long questionId = 1L;
		LocalDateTime deletedTime = LocalDateTime.now();

		DailyQuestion dailyQuestion = new DailyQuestion("질문", "01-01", DailyQuestionStatus.DELETED);
		ReflectionTestUtils.setField(dailyQuestion, "id", 1L);
		ReflectionTestUtils.setField(dailyQuestion, "deletedAt", deletedTime);

		given(questionRepository.findDailyQuestionByStatusAndId(List.of(DailyQuestionStatus.DELETED), questionId))
			.willReturn(Optional.of(dailyQuestion));
		given(questionRepository.findDailyQuestionStatusById(questionId))
			.willReturn(Optional.of(DailyQuestionStatus.DELETED));


		// when
		questionService.restoreDailyQuestion(questionId);

		// then
		assertThat(dailyQuestion.getDailyQuestionStatus()).isEqualTo(DailyQuestionStatus.ACTIVE);
		assertThat(dailyQuestion.getDeletedAt()).isNull();
	}
}
