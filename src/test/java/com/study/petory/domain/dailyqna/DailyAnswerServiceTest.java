package com.study.petory.domain.dailyqna;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.study.petory.domain.dailyqna.dto.request.DailyAnswerCreateRequestDto;
import com.study.petory.domain.dailyqna.dto.request.DailyAnswerUpdateRequestDto;
import com.study.petory.domain.dailyqna.dto.response.DailyAnswerGetDeletedResponse;
import com.study.petory.domain.dailyqna.dto.response.DailyAnswerGetHiddenResponseDto;
import com.study.petory.domain.dailyqna.dto.response.DailyAnswerGetResponseDto;
import com.study.petory.domain.dailyqna.entity.DailyAnswer;
import com.study.petory.domain.dailyqna.entity.DailyAnswerStatus;
import com.study.petory.domain.dailyqna.entity.DailyQuestion;
import com.study.petory.domain.dailyqna.entity.DailyQuestionStatus;
import com.study.petory.domain.dailyqna.repository.DailyAnswerRepository;
import com.study.petory.domain.dailyqna.service.DailyAnswerServiceImpl;
import com.study.petory.domain.dailyqna.service.DailyQuestionServiceImpl;
import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserPrivateInfo;
import com.study.petory.domain.user.entity.UserRole;
import com.study.petory.domain.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class DailyAnswerServiceTest {

	@InjectMocks
	private DailyAnswerServiceImpl dailyQnaService;

	@Mock
	private DailyAnswerRepository dailyAnswerRepository;

	@Mock
	private UserService userService;

	@Mock
	private DailyQuestionServiceImpl questionService;

	private final UserPrivateInfo testUserInfo = new UserPrivateInfo(
		"1L",
		"실명",
		"000-0000-0000"
	);

	private final List<UserRole> testUserRole = new ArrayList<>(List.of(new UserRole(Role.USER)));

	private final User testUser = new User(
		"닉네임",
		"email@email.com",
		testUserInfo,
		testUserRole
	);

	private final DailyQuestion testDailyQuestion = new DailyQuestion(
		"오늘은 뭘 먹었어?",
		"01-01",
		DailyQuestionStatus.ACTIVE
	);

	// 더미 DailyQna 생성 메서드
	private DailyAnswer setDailyQna(String answer, DailyAnswerStatus dailyAnswerStatus, String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DailyAnswer dummyDailyAnswer = DailyAnswer.builder()
			.user(testUser)
			.dailyQuestion(testDailyQuestion)
			.answer(answer)
			.dailyAnswerStatus(dailyAnswerStatus)
			.build();
		ReflectionTestUtils.setField(dummyDailyAnswer, "createdAt", LocalDateTime.parse(date, formatter));
		if (dailyAnswerStatus.equals(DailyQuestionStatus.DELETED)) {
			ReflectionTestUtils.setField(dummyDailyAnswer, "deletedAt", LocalDateTime.now());
		}
		return dummyDailyAnswer;
	}

	// 더미 DailyQna 생성 메서드
	private Page<DailyAnswer> setDailyQnaPage(int total, DailyAnswerStatus dailyAnswerStatus, Pageable pageable) {
		List<DailyAnswer> dailyAnswerList = new ArrayList<>();
		for (int i = 1; i <= total; i++) {
			DailyAnswer dailyAnswer = setDailyQna(
				"답변 " + i,
				dailyAnswerStatus,
				"2023-01-01 00:00:00"
			);
			if (dailyAnswerStatus == DailyAnswerStatus.DELETED) {
				dailyAnswer.deactivateEntity();
			}
			dailyAnswerList.add(dailyAnswer);
		}
		int start = (int)pageable.getOffset();
		int end = Math.min(start + pageable.getPageSize(), dailyAnswerList.size());
		List<DailyAnswer> startAndEnd = dailyAnswerList.subList(start, end);
		return new PageImpl<>(startAndEnd, pageable, dailyAnswerList.size());
	}

	@Test
	@DisplayName("사용자가 질문에 대한 답변을 저장한다.")
	public void saveDailyQna() {
		// given
		Long userId = 1L;
		Long questionId = 1L;

		DailyAnswerCreateRequestDto requestDto = new DailyAnswerCreateRequestDto("답변");

		given(userService.findUserById(userId))
			.willReturn(testUser);
		given(questionService.findDailyQuestionByIdAndStatus(List.of(DailyQuestionStatus.ACTIVE), questionId))
			.willReturn(testDailyQuestion);

		// when
		dailyQnaService.saveDailyAnswer(userId, questionId, requestDto);
		// then
		verify(dailyAnswerRepository, times(1)).save(any(DailyAnswer.class));
	}

	@Test
	@DisplayName("질문에 사용자가 남긴 모든 답변을 조회한다.")
	public void getAllAnswer() {
		// given
		Long userId = 1L;
		Long questionId = 1L;

		DailyAnswer d1 = setDailyQna("답변 1", DailyAnswerStatus.ACTIVE, "2022-01-01 00:00:00");
		DailyAnswer d2 = setDailyQna("답변 2", DailyAnswerStatus.ACTIVE, "2024-01-01 00:00:00");
		DailyAnswer d3 = setDailyQna("답변 3", DailyAnswerStatus.ACTIVE, "2023-01-01 00:00:00");

		List<DailyAnswer> responseList = new ArrayList<>(Arrays.asList(d1, d3, d2));

		given(dailyAnswerRepository.findDailyAnswer(userId, questionId))
			.willReturn(responseList);

		// when
		List<DailyAnswerGetResponseDto> response = dailyQnaService.findDailyAnswer(userId, questionId);

		// then
		assertThat(response).hasSize(3);
		assertThat(response.get(0).getAnswer()).isEqualTo("답변 1");
		assertThat(response.get(1).getAnswer()).isEqualTo("답변 3");
		assertThat(response.get(2).getAnswer()).isEqualTo("답변 2");
	}

	@Test
	@DisplayName("사용자가 답변을 수정한다.")
	public void updateDailyQna() {
		// given
		Long userId = 1L;
		Long dailyQnaId = 1L;

		DailyAnswer dailyAnswer = setDailyQna("수정 전 답변", DailyAnswerStatus.ACTIVE, "2022-01-01 00:00:00");
		ReflectionTestUtils.setField(dailyAnswer, "id", 1L);
		ReflectionTestUtils.setField(testUser, "id", 1L);

		given(dailyAnswerRepository.findDailyAnswerByStatusAndId(List.of(DailyAnswerStatus.ACTIVE), dailyQnaId))
			.willReturn(Optional.of(dailyAnswer));

		DailyAnswerUpdateRequestDto request = new DailyAnswerUpdateRequestDto("수정 후 답변");

		// when
		dailyQnaService.updateDailyAnswer(userId, dailyQnaId, request);

		// then
		assertThat(dailyAnswer.getAnswer()).isEqualTo("수정 후 답변");
	}

	@Test
	@DisplayName("사용자가 답변을 숨김 처리한다.")
	public void hideDailyQna() {
		// given
		Long userId = 1L;
		Long dailyQnaId = 1L;

		DailyAnswer dailyAnswer = setDailyQna("숨기기 전 답변", DailyAnswerStatus.ACTIVE, "2022-01-01 00:00:00");
		ReflectionTestUtils.setField(dailyAnswer, "id", 1L);
		ReflectionTestUtils.setField(testUser, "id", 1L);

		given(dailyAnswerRepository.findDailyAnswerByStatusAndId(List.of(DailyAnswerStatus.ACTIVE), dailyQnaId))
			.willReturn(Optional.of(dailyAnswer));
		given(dailyAnswerRepository.findDailyAnswerStatusById(dailyQnaId))
			.willReturn(Optional.of(DailyAnswerStatus.ACTIVE));

		// when
		dailyQnaService.hideDailyAnswer(userId, dailyQnaId);

		//then
		assertThat(dailyAnswer.getDailyAnswerStatus()).isEqualTo(DailyAnswerStatus.HIDDEN);
	}

	@Test
	@DisplayName("사용자가 숨김 처리한 답변을 조회한다.")
	public void findHiddenDailyQna() {
		// given
		Long userId = 1L;

		Pageable pageable = PageRequest.of(1, 50, Sort.by("date").ascending());
		given(dailyAnswerRepository.findDailyAnswerPageByStatus(List.of(DailyAnswerStatus.HIDDEN), userId, pageable))
			.willReturn(setDailyQnaPage(60, DailyAnswerStatus.HIDDEN, pageable));

		// when
		Page<DailyAnswerGetHiddenResponseDto> responsePage = dailyQnaService.findHiddenDailyAnswer(userId, pageable);

		// then
		assertThat(responsePage.getContent()).hasSize(10);
		assertThat(responsePage.getTotalElements()).isEqualTo(60);
		assertThat(responsePage.getTotalPages()).isEqualTo(2);
		assertThat(responsePage.isLast()).isTrue();
	}

	@Test
	@DisplayName("사용자가 숨김 처리한 답변을 복구한다.")
	public void updateDailyQnaStatusActive() {
		// given
		Long userId = 1L;
		Long dailyQnaId = 1L;

		DailyAnswer dailyAnswer = setDailyQna("삭제 전 답변", DailyAnswerStatus.HIDDEN, "2022-01-01 00:00:00");
		ReflectionTestUtils.setField(dailyAnswer, "id", 1L);
		ReflectionTestUtils.setField(testUser, "id", 1L);

		given(dailyAnswerRepository.findDailyAnswerByStatusAndId(List.of(DailyAnswerStatus.HIDDEN), dailyQnaId))
			.willReturn(Optional.of(dailyAnswer));
		given(dailyAnswerRepository.findDailyAnswerStatusById(dailyQnaId))
			.willReturn(Optional.of(DailyAnswerStatus.HIDDEN));

		// when
		dailyQnaService.updateDailyAnswerStatusActive(userId, dailyQnaId);

		// then
		assertThat(dailyAnswer.getDailyAnswerStatus()).isEqualTo(DailyAnswerStatus.ACTIVE);
	}

	@Test
	@DisplayName("관리자가 답변을 삭제한다.")
	public void deleteDailyQna() {
		// given
		Long dailyQnaId = 1L;

		DailyAnswer dailyAnswer = setDailyQna("삭제 전 답변", DailyAnswerStatus.ACTIVE, "2022-01-01 00:00:00");
		ReflectionTestUtils.setField(dailyAnswer, "id", 1L);

		given(dailyAnswerRepository.findDailyAnswerByStatusAndId(
			List.of(DailyAnswerStatus.ACTIVE, DailyAnswerStatus.HIDDEN),
			dailyQnaId))
			.willReturn(Optional.of(dailyAnswer));
		given(dailyAnswerRepository.findDailyAnswerStatusById(dailyQnaId))
			.willReturn(Optional.of(DailyAnswerStatus.ACTIVE));

		// when
		dailyQnaService.deleteDailyAnswer(dailyQnaId);

		// then
		assertThat(dailyAnswer.getDailyAnswerStatus()).isEqualTo(DailyAnswerStatus.DELETED);
		assertThat(dailyAnswer.getDeletedAt()).isNotNull();
	}

	@Test
	@DisplayName("관리자가 삭제된 답변 조회")
	public void findDeletedDailyQna() {
		// given
		Long userId = 1L;

		Pageable pageable = PageRequest.of(1, 50, Sort.by("date").ascending());

		given(
			dailyAnswerRepository.findDailyAnswerPageByStatus(List.of(DailyAnswerStatus.DELETED), userId,
				pageable)).willReturn(
			setDailyQnaPage(60, DailyAnswerStatus.DELETED, pageable));

		// when
		Page<DailyAnswerGetDeletedResponse> responsePage = dailyQnaService.findDeletedDailyAnswer(userId, pageable);

		// then
		assertThat(responsePage.getContent()).hasSize(10);
		assertThat(responsePage.getTotalElements()).isEqualTo(60);
		assertThat(responsePage.getTotalPages()).isEqualTo(2);
		assertThat(responsePage.isLast()).isTrue();
	}

	@Test
	@DisplayName("관리자가 삭제된 답변 복구")
	public void restoreDailyQna() {
		// given
		Long dailyQnaId = 1L;

		DailyAnswer deletedDailyAnswer = setDailyQna("삭제 전 답변", DailyAnswerStatus.DELETED, "2022-01-01 00:00:00");
		ReflectionTestUtils.setField(deletedDailyAnswer, "id", 1L);

		given(dailyAnswerRepository.findDailyAnswerByStatusAndId(List.of(DailyAnswerStatus.DELETED), dailyQnaId))
			.willReturn(Optional.of(deletedDailyAnswer));
		given(dailyAnswerRepository.findDailyAnswerStatusById(dailyQnaId))
			.willReturn(Optional.of(DailyAnswerStatus.DELETED));

		// when
		dailyQnaService.restoreDailyAnswer(dailyQnaId);

		// then
		assertThat(deletedDailyAnswer.getDailyAnswerStatus()).isEqualTo(DailyAnswerStatus.ACTIVE);
		assertThat(deletedDailyAnswer.getDeletedAt()).isNull();

	}
}
