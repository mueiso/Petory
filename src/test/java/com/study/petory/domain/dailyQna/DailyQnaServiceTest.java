package com.study.petory.domain.dailyQna;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import com.study.petory.common.util.EntityFetcher;
import com.study.petory.domain.dailyQna.Repository.DailyQnaRepository;
import com.study.petory.domain.dailyQna.dto.request.DailyQnaCreateRequestDto;
import com.study.petory.domain.dailyQna.entity.DailyQna;
import com.study.petory.domain.dailyQna.entity.Question;
import com.study.petory.domain.dailyQna.service.DailyQnaServiceImpl;
import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserPrivateInfo;
import com.study.petory.domain.user.entity.UserRole;

@ExtendWith(MockitoExtension.class)
public class DailyQnaServiceTest {

	@InjectMocks
	private DailyQnaServiceImpl dailyQnaService;

	@Mock
	private DailyQnaRepository dailyQnaRepository;

	@Mock
	private EntityFetcher entityFetcher;

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

	@Test
	@DisplayName("질문에 대한 답변을 저장한다.")
	public void saveDailyQna() {
		// given
		testUserRole.add(userRole);
		DailyQnaCreateRequestDto requestDto = new DailyQnaCreateRequestDto("답변");

		Long userId = 1L;
		Long questionId = 1L;

		given(entityFetcher.findUserByUserId(userId)).willReturn(testUser);
		given(entityFetcher.findQuestionByQuestionId(questionId)).willReturn(testQuestion);

		// when
		dailyQnaService.saveDailyQNA(userId, questionId, requestDto);
		// then
		verify(dailyQnaRepository, times(1)).save(any(DailyQna.class));
	}
}
