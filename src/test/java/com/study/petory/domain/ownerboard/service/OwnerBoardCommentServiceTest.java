package com.study.petory.domain.ownerboard.service;

import static com.study.petory.domain.user.entity.UserStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import com.study.petory.domain.ownerboard.dto.request.OwnerBoardCommentCreateRequestDto;
import com.study.petory.domain.ownerboard.dto.request.OwnerBoardCommentUpdateRequestDto;
import com.study.petory.domain.ownerboard.dto.response.OwnerBoardCommentCreateResponseDto;
import com.study.petory.domain.ownerboard.dto.response.OwnerBoardCommentGetResponseDto;
import com.study.petory.domain.ownerboard.dto.response.OwnerBoardCommentUpdateResponseDto;
import com.study.petory.domain.ownerboard.entity.OwnerBoard;
import com.study.petory.domain.ownerboard.entity.OwnerBoardComment;
import com.study.petory.domain.ownerboard.repository.OwnerBoardCommentRepository;
import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserPrivateInfo;
import com.study.petory.domain.user.entity.UserRole;
import com.study.petory.domain.user.entity.UserStatus;
import com.study.petory.domain.user.repository.UserRepository;
import com.study.petory.domain.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class OwnerBoardCommentServiceTest {

	@Mock
	private OwnerBoardCommentRepository ownerBoardCommentRepository;

	@Mock
	private OwnerBoardService ownerBoardService;

	@Mock
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private OwnerBoardCommentServiceImpl ownerBoardCommentService;

	private User mockUser;
	private OwnerBoard mockBoard;

	// @BeforeEach
	// void SetUser() {
	// 	List<UserRole> userRole = new ArrayList<>();
	// 	userRole.add(new UserRole(Role.USER));
	// 	UserPrivateInfo info = new UserPrivateInfo("google", "name", "010-0000-0000");
	// 	this.mockUser = new User("nickname", "test@mail.com", info, userRole);
	// 	ReflectionTestUtils.setField(mockUser, "id", 1L);
	// }

	@BeforeEach
	void SetBoard() {
		this.mockBoard = new OwnerBoard("제목", "내용", mockUser);
		ReflectionTestUtils.setField(mockBoard, "id", 10L);
	}

	@Test
	void 댓글_생성에_성공한다() {
		// given
		Long userId = 1L;
		User user = createActiveUser(userId);
		Long boardId = 10L;
		OwnerBoardCommentCreateRequestDto requestDto = new OwnerBoardCommentCreateRequestDto("새 댓글");

		OwnerBoardComment comment = OwnerBoardComment.builder()
			.content("새 댓글")
			.user(mockUser)
			.ownerBoard(mockBoard)
			.build();

		given(userService.findUserById(userId)).willReturn(user);
		given(ownerBoardService.findOwnerBoardById(boardId)).willReturn(mockBoard);
		given(ownerBoardCommentRepository.save(any())).willReturn(comment);

		// when
		OwnerBoardCommentCreateResponseDto result =
			ownerBoardCommentService.saveOwnerBoardComment(userId, boardId, requestDto);

		// then
		assertEquals("새 댓글", result.getContent());
	}

	@Test
	void 댓글_전체_조회에_성공한다() {
		// given
		Long boardId = 1L;
		Pageable pageable = PageRequest.of(0, 10);

		List<OwnerBoardComment> mockList = IntStream.range(0, 15)
			.mapToObj(i -> OwnerBoardComment.builder()
				.content("댓글 " + i)
				.user(mockUser)
				.ownerBoard(mockBoard)
				.build())
			.toList();

		Page<OwnerBoardComment> mockPage = new PageImpl<>(
			mockList.subList(0, 10), pageable, mockList.size());

		given(ownerBoardCommentRepository.findByOwnerBoardId(boardId, pageable)).willReturn(mockPage);

		// when
		Page<OwnerBoardCommentGetResponseDto> result = ownerBoardCommentService.findAllOwnerBoardComments(
			boardId, pageable);

		// then
		assertEquals(15, result.getTotalElements());
		assertEquals(10, result.getContent().size());
		assertEquals("댓글 1", result.getContent().get(1).getContent());
	}

	// @Test
	// void 댓글_수정에_성공한다() {
	// 	// given
	// 	Long boardId = 10L;
	// 	Long commentId = 100L;
	//
	// 	OwnerBoardComment comment = OwnerBoardComment.builder()
	// 		.content("기존 댓글")
	// 		.user(mockUser)
	// 		.ownerboard(mockBoard)
	// 		.build();
	// 	ReflectionTestUtils.setField(comment, "id", commentId);
	//
	// 	given(ownerBoardCommentRepository.findById(commentId)).willReturn(Optional.of(comment));
	//
	// 	OwnerBoardCommentUpdateRequestDto requestDto = new OwnerBoardCommentUpdateRequestDto("수정된 댓글");
	//
	// 	// when
	// 	OwnerBoardCommentUpdateResponseDto result = ownerBoardCommentService.updateOwnerBoardComment(
	// 		boardId, commentId, requestDto);
	//
	// 	// then
	// 	assertEquals("수정된 댓글", result.getContent());
	// }
	//
	// @Test
	// void 댓글_삭제에_성공한다() {
	// 	// given
	// 	Long boardId = 10L;
	// 	Long commentId = 100L;
	//
	// 	OwnerBoardComment comment = OwnerBoardComment.builder()
	// 		.content("삭제할 댓글")
	// 		.user(mockUser)
	// 		.ownerboard(mockBoard)
	// 		.build();
	// 	ReflectionTestUtils.setField(comment, "id", commentId);
	//
	// 	given(ownerBoardCommentRepository.findById(commentId)).willReturn(Optional.of(comment));
	//
	// 	// when
	// 	ownerBoardCommentService.deleteOwnerBoardComment(boardId, commentId);
	//
	// 	// then
	// 	assertNotNull(comment.getDeletedAt());
	//
	// }

	// 기본 활성 유저 생성
	private User createActiveUser(Long userId) {
		User user = createUserWithStatus(ACTIVE);

		// user Id 주입
		ReflectionTestUtils.setField(user, "id", userId);

		return user;
	}

	// 테스트용 유저 객체를 생성하는 유틸 메서드
	private User createUserWithStatus(UserStatus status) {

		UserPrivateInfo privateInfo = UserPrivateInfo.builder()
			.authId("auth123")
			.name("이름")
			.mobileNum("01012345678")
			.build();

		User user = User.builder()
			.nickname("닉네임")
			.email("test@email.com")
			.userPrivateInfo(privateInfo)
			.userRole(new ArrayList<>(List.of(UserRole.builder().role(Role.USER).build())))
			.build();

		// 전달받은 상태로 userStatus 설정
		user.updateStatus(status);

		return user;
	}

	// 게시글 등록 유틸 메서드
	private OwnerBoard createOwnerBoard() {

		User user = createActiveUser(1L);

		OwnerBoard ownerBoard = OwnerBoard.builder()
			.title("제목")
			.content("내용")
			.user(user)
			.build();

		return ownerBoard;
	}
}
