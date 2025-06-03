package com.study.petory.domain.dailyQna;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.study.petory.domain.dailyQna.Repository.DailyQnaRepository;
import com.study.petory.domain.dailyQna.dto.request.DailyQnaCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.request.DailyQnaUpdateRequestDto;
import com.study.petory.domain.dailyQna.dto.response.DailyQnaGetResponseDto;
import com.study.petory.domain.dailyQna.entity.DailyQna;
import com.study.petory.domain.dailyQna.entity.Question;
import com.study.petory.domain.dailyQna.service.DailyQnaServiceImpl;
import com.study.petory.domain.dailyQna.service.QuestionServiceImpl;
import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserPrivateInfo;
import com.study.petory.domain.user.entity.UserRole;
import com.study.petory.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class DailyQnaServiceTest {

	@InjectMocks
	private DailyQnaServiceImpl dailyQnaService;

	@Mock
	private DailyQnaRepository dailyQnaRepository;

	// 리펙토링 예정
	@Mock
	private UserRepository userRepository;

	@Mock
	private QuestionServiceImpl questionService;

	private final UserPrivateInfo testUserInfo = new UserPrivateInfo(
		1L,
		"실명",
		"000-0000-0000"
	);
	private final UserRole userRole = new UserRole(Role.USER);
	private final List<UserRole> testUserRole = new ArrayList<>();
	private final User testUser = new User(
		"닉네임",
		"email@email.com",
		testUserInfo,
		testUserRole
	);
	private final Question testQuestion = new Question(
		"오늘은 뭘 먹었어?",
		"01-01"
	);

	// 더미 DailyQna 생성 메서드
	private DailyQna createDummyDailyQna(String answer, LocalDateTime date) {
		DailyQna qna = new DailyQna(testUser, testQuestion, answer);
		ReflectionTestUtils.setField(qna, "createdAt", date);
		return qna;
	}

	// 날짜의 정렬을 확인하는 메서드
	private void asserSortedByCreatedAtDesc(List<DailyQnaGetResponseDto> dtoList) {
		List<LocalDateTime> resultCreatedAt = dtoList.stream()
			.map(DailyQnaGetResponseDto::getCreatedAt)
			.collect(Collectors.toList());

		List<LocalDateTime> sortedCreatedAt = new ArrayList<>(resultCreatedAt);
		sortedCreatedAt.sort(Comparator.reverseOrder());

		assertThat(resultCreatedAt).isEqualTo(sortedCreatedAt);
	}

	@Test
	@DisplayName("질문에 대한 답변을 저장한다.")
	public void saveDailyQna() {
		// given
		testUserRole.add(userRole);
		DailyQnaCreateRequestDto requestDto = new DailyQnaCreateRequestDto("답변");

		Long userId = 1L;
		Long questionId = 1L;

		given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
		given(questionService.findQuestionByQuestionIdOrElseThrow(questionId)).willReturn(testQuestion);

		// when
		dailyQnaService.saveDailyQna(userId, questionId, requestDto);
		// then
		verify(dailyQnaRepository, times(1)).save(any(DailyQna.class));
	}

	@Test
	@DisplayName("질문에 사용자가 남긴 모든 답변을 조회한다.")
	public void getAllAnswer() {
		// given
		testUserRole.add(userRole);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		DailyQnaGetResponseDto d1 = new DailyQnaGetResponseDto("답변 1", LocalDateTime.parse("2022-01-01 00:00:00", formatter));
		DailyQnaGetResponseDto d2 = new DailyQnaGetResponseDto("답변 2", LocalDateTime.parse("2024-01-01 00:00:00", formatter));
		DailyQnaGetResponseDto d3 = new DailyQnaGetResponseDto("답변 3", LocalDateTime.parse("2023-01-01 00:00:00", formatter));

		List<DailyQnaGetResponseDto> responseList = new ArrayList<>(Arrays.asList(d1, d2, d3));

		Long userId = 1L;
		Long questionId = 1L;

		given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
		given(questionService.findQuestionByQuestionIdOrElseThrow(questionId)).willReturn(testQuestion);
		given(dailyQnaRepository.findDailyQna(testUser, testQuestion)).willReturn(responseList);

		// when
		List<DailyQnaGetResponseDto> response = dailyQnaService.findDailyQna(userId, questionId);

		// then
		assertThat(response).hasSize(3);
		assertThat(response.get(0).getAnswer()).isEqualTo("답변 2");
		assertThat(response.get(1).getAnswer()).isEqualTo("답변 3");
		assertThat(response.get(2).getAnswer()).isEqualTo("답변 1");

		asserSortedByCreatedAtDesc(response);
	}

	@Test
	@DisplayName("사용자가 답변을 수정한다.")
	public void updateDailyQna() {
		// given
		testUserRole.add(userRole);

		DailyQna savedQna = new DailyQna(testUser, testQuestion, "수정 전 답변");
		ReflectionTestUtils.setField(savedQna, "id", 1L);
		ReflectionTestUtils.setField(testUser, "id", 1L);

		Long userId = 1L;
		Long dailyQnaId = 1L;

		given(dailyQnaRepository.findById(dailyQnaId)).willReturn(Optional.of(savedQna));

		DailyQnaUpdateRequestDto updateData = new DailyQnaUpdateRequestDto("수정 후 답변");

		// when
		dailyQnaService.updateDailyQna(userId, dailyQnaId, updateData);

		// then
		DailyQna updateQna = dailyQnaService.findDailyQnaByDailyQnaIdOrElseThrow(dailyQnaId);

		assertThat(updateQna.getAnswer()).isEqualTo("수정 후 답변");
	}

	@Test
	@DisplayName("사용자가 답변을 삭제한다.")
	public void deleteDailyQna() {
		// given
		testUserRole.add(userRole);

		DailyQna savedQna = new DailyQna(testUser, testQuestion, "삭제 전 답변");
		ReflectionTestUtils.setField(savedQna, "id", 1L);
		ReflectionTestUtils.setField(testUser, "id", 1L);

		Long userId = 1L;
		Long dailyQnaId = 1L;

		given(dailyQnaRepository.findById(dailyQnaId)).willReturn(Optional.of(savedQna));

		// when
		dailyQnaService.deleteDailyQna(userId, dailyQnaId);

		// then
		assertThat(savedQna.getDeletedAt()).isNotNull();
	}

}
