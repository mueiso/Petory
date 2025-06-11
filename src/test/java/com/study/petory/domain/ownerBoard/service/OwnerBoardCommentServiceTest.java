package com.study.petory.domain.ownerBoard.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardCommentCreateRequestDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCommentCreateResponseDto;
import com.study.petory.domain.ownerBoard.entity.OwnerBoard;
import com.study.petory.domain.ownerBoard.entity.OwnerBoardComment;
import com.study.petory.domain.ownerBoard.repository.OwnerBoardCommentRepository;
import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserPrivateInfo;
import com.study.petory.domain.user.entity.UserRole;
import com.study.petory.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class OwnerBoardCommentServiceTest {

	@Mock
	private OwnerBoardCommentRepository ownerBoardCommentRepository;

	@Mock
	private OwnerBoardService ownerBoardService;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private OwnerBoardCommentServiceImpl ownerBoardCommentService;

	private User mockUser;
	private OwnerBoard mockBoard;

	@BeforeEach
	void SetUser() {
		List<UserRole> userRole = new ArrayList<>();
		userRole.add(new UserRole(Role.USER));
		UserPrivateInfo info = new UserPrivateInfo(1L, "name", "010-0000-0000");
		this.mockUser = new User("nickname", "test@mail.com", info, userRole);
		ReflectionTestUtils.setField(mockUser, "id", 1L);
	}

	@BeforeEach
	void SetBoard() {
		this.mockBoard = new OwnerBoard("제목", "내용", mockUser);
		ReflectionTestUtils.setField(mockBoard, "id", 1L);
	}

	@Test
	void 댓글_생성에_성공한다() {
		// given
		Long userId = 1L;
		Long boardId = 1L;
		OwnerBoardCommentCreateRequestDto requestDto = new OwnerBoardCommentCreateRequestDto("새 댓글");

		OwnerBoardComment comment = OwnerBoardComment.builder()
			.content("새 댓글")
			.user(mockUser)
			.ownerBoard(mockBoard)
			.build();

		given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
		given(ownerBoardService.findOwnerBoardById(boardId)).willReturn(mockBoard);
		given(ownerBoardCommentRepository.save(any())).willReturn(comment);

		// when
		OwnerBoardCommentCreateResponseDto result =
			ownerBoardCommentService.saveOwnerBoardComment(boardId, requestDto);

		// then
		assertEquals("새 댓글", result.getContent());
	}


	// 댓글 조회
	// 댓글 수정
	// 댓글 삭제

}
