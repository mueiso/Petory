package com.study.petory.domain.ownerBoard.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardCreateRequestDto;
import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardUpdateRequestDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCreateResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardGetAllResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardGetResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardUpdateResponseDto;
import com.study.petory.domain.ownerBoard.entity.OwnerBoard;
import com.study.petory.domain.ownerBoard.entity.OwnerBoardComment;
import com.study.petory.domain.ownerBoard.entity.OwnerBoardImage;
import com.study.petory.domain.ownerBoard.repository.OwnerBoardCommentRepository;
import com.study.petory.domain.ownerBoard.repository.OwnerBoardRepository;
import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserPrivateInfo;
import com.study.petory.domain.user.entity.UserRole;
import com.study.petory.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class OwnerBoardServiceTest {

	@Mock
	private OwnerBoardRepository ownerBoardRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private OwnerBoardCommentRepository ownerBoardCommentRepository;

	@Mock
	private OwnerBoardImageService ownerBoardImageService;

	@InjectMocks
	private OwnerBoardServiceImpl ownerBoardService;

	private User mockUser;

	@BeforeEach
	void SetUser() {
		List<UserRole> userRole = new ArrayList<>();
		userRole.add(new UserRole(Role.USER));
		UserPrivateInfo info = new UserPrivateInfo(1L, "name", "010-0000-0000");
		this.mockUser = new User("nickname", "test@mail.com", info, userRole);
		ReflectionTestUtils.setField(mockUser, "id", 1L);
	}

	@Test
	void 게시글_저장에_성공한다_이미지_포함() {
		// given
		OwnerBoardCreateRequestDto requestDto = new OwnerBoardCreateRequestDto("제목", "내용");

		List<MultipartFile> images = List.of(
			new MockMultipartFile("image", "test.jpg", "image/jpeg", "image data".getBytes())
		);

		OwnerBoard mockBoard = OwnerBoard.builder()
			.title("제목")
			.content("내용")
			.user(mockUser)
			.build();

		List<String> mockUrls = List.of("https://s3.url/test1.jpg", "https://s3.url/test2.jpg");

		given(userRepository.findById(1L)).willReturn(Optional.of(mockUser));
		given(ownerBoardRepository.save(any(OwnerBoard.class))).willReturn(mockBoard);
		given(ownerBoardImageService.uploadAndSaveAll(any(), any())).willReturn(mockUrls);

		// when
		OwnerBoardCreateResponseDto result = ownerBoardService.saveOwnerBoard(requestDto, images);

		// then
		assertThat(result.getTitle()).isEqualTo("제목");
		assertThat(result.getContent()).isEqualTo("내용");
		assertThat(result.getImageUrls()).containsExactlyElementsOf(mockUrls);
	}

	@Test
	void 게시글_저장에_성공한다_이미지_미포함() {
		// given
		OwnerBoardCreateRequestDto requestDto = new OwnerBoardCreateRequestDto("제목", "내용");
		List<MultipartFile> images = null;

		OwnerBoard mockBoard = OwnerBoard.builder()
			.title("제목")
			.content("내용")
			.user(mockUser)
			.build();

		given(userRepository.findById(1L)).willReturn(Optional.of(mockUser));
		given(ownerBoardRepository.save(any(OwnerBoard.class))).willReturn(mockBoard);

		// when
		OwnerBoardCreateResponseDto result = ownerBoardService.saveOwnerBoard(requestDto, images);

		// then
		assertThat(result.getTitle()).isEqualTo("제목");
		assertThat(result.getContent()).isEqualTo("내용");
		assertTrue(result.getImageUrls().isEmpty());
		verify(ownerBoardImageService, never()).uploadAndSaveAll(any(), any());
	}

	@Test
	void 제목이_포함된_게시글_조회에_성공한다() {
		// given
		String keyword = "제목";
		Pageable pageable = PageRequest.of(0, 5);
		List<OwnerBoard> mockList = List.of(
			OwnerBoard.builder().title("제목입니다").content("내용").build(),
			OwnerBoard.builder().title("포함안된 글").content("내용").build()
		);

		Page<OwnerBoard> filteredPage = new PageImpl<>(
			mockList.stream()
				.filter(board -> board.getTitle().contains(keyword))
				.collect(Collectors.toList())
		);

		given(ownerBoardRepository.findByTitleContaining(keyword, pageable)).willReturn(filteredPage);

		// when
		Page<OwnerBoardGetAllResponseDto> result = ownerBoardService.findAllOwnerBoards(keyword, pageable);

		// then
		assertEquals(1, result.getTotalElements());
		assertEquals("제목입니다", result.getContent().get(0).getTitle());
		verify(ownerBoardRepository, times(1)).findByTitleContaining(keyword, pageable);
		verify(ownerBoardRepository, never()).findAll();
	}

	@Test
	void 제목_검색어_없이_게시글_전체_조회에_성공한다() {
		// given
		Pageable pageable = PageRequest.of(0, 5);
		List<OwnerBoard> mockList = List.of(
			OwnerBoard.builder().title("제목입니다").content("내용").build(),
			OwnerBoard.builder().title("두번째 글").content("내용").build()
		);

		Page<OwnerBoard> mockPage = new PageImpl<>(mockList);

		given(ownerBoardRepository.findAll(pageable)).willReturn(mockPage);

		// when
		Page<OwnerBoardGetAllResponseDto> result = ownerBoardService.findAllOwnerBoards(null, pageable);

		// then
		assertEquals(2, result.getTotalElements());
		verify(ownerBoardRepository, times(1)).findAll(pageable);
	}

	@Test
	void 게시글_단건_조회에_성공한다() {
		// given
		Long boardId = 1L;
		OwnerBoard mockBoard = OwnerBoard.builder()
			.title("제목")
			.content("내용")
			.user(mockUser)
			.build();

		List<OwnerBoardComment> mockComments = IntStream.range(0, 10)
			.mapToObj(i -> OwnerBoardComment.builder()
				.content("댓글 " + i)
				.user(mockUser)
				.ownerBoard(mockBoard)
				.build())
			.toList();

		ReflectionTestUtils.setField(mockBoard, "id", boardId);
		ReflectionTestUtils.setField(mockBoard, "images", List.of("mockImage1", "mockImage2"));
		ReflectionTestUtils.setField(mockBoard, "comments", mockComments);

		given(ownerBoardRepository.findByIdWithImages(boardId)).willReturn(Optional.of(mockBoard));
		given(ownerBoardCommentRepository.findTop10ByOwnerBoardIdOrderByCreatedAt(boardId)).willReturn(mockComments);

		// when
		OwnerBoardGetResponseDto result = ownerBoardService.findOwnerBoard(boardId);

		// then
		assertEquals("제목", result.getTitle());
		assertEquals("내용", result.getContent());
		assertEquals(2, result.getImages().size());
		assertEquals(10, result.getCommentsList().size());
	}

	@Test
	void 게시글_수정에_성공한다() {
		// given
		Long boardId = 1L;
		OwnerBoard originalBoard = OwnerBoard.builder()
			.title("원래 제목")
			.content("원래 내용")
			.user(mockUser)
			.build();

		ReflectionTestUtils.setField(originalBoard, "id", boardId);
		OwnerBoardUpdateRequestDto requestDto = new OwnerBoardUpdateRequestDto("수정된 제목", "수정된 내용");

		given(ownerBoardRepository.findByIdWithImages(boardId)).willReturn(Optional.of(originalBoard));

		// when
		OwnerBoardUpdateResponseDto result = ownerBoardService.updateOwnerBoard(boardId, requestDto);

		// then
		assertEquals("수정된 제목", result.getTitle());
		assertEquals("수정된 내용", result.getContent());
	}

	@Test
	void 게시글_삭제에_성공한다() {
		// given
		Long boardId = 1L;

		OwnerBoardImage image1 = mock(OwnerBoardImage.class);
		OwnerBoardImage image2 = mock(OwnerBoardImage.class);

		OwnerBoard board = OwnerBoard.builder()
			.title("제목")
			.content("내용")
			.user(mockUser)
			.build();
		ReflectionTestUtils.setField(board, "id", boardId);
		ReflectionTestUtils.setField(board, "images", new ArrayList<>(List.of(image1, image2)));

		given(ownerBoardRepository.findByIdWithImages(boardId)).willReturn(Optional.of(board));

		// when
		ownerBoardService.deleteOwnerBoard(boardId);

		// then
		verify(ownerBoardImageService, times(2)).deleteImage(any());
		assertTrue(board.getImages().isEmpty());
		assertNotNull(board.getDeletedAt());
	}

	@Test
	void 게시글_복구에_성공한다() {
		// given
		Long boardId = 1L;

		OwnerBoard deletedBoard = OwnerBoard.builder()
			.title("삭제된 제목")
			.content("삭제된 내용")
			.user(mockUser)
			.build();
		ReflectionTestUtils.setField(deletedBoard, "id", boardId);
		ReflectionTestUtils.setField(deletedBoard, "deletedAt", LocalDateTime.now());

		given(ownerBoardRepository.findByIdIncludingDeleted(boardId)).willReturn(Optional.of(deletedBoard));

		// when
		ownerBoardService.restoreOwnerBoard(boardId);

		// then
		assertNull(deletedBoard.getDeletedAt());
	}

	@Test
	void 게시글의_사진_삭제에_성공한다() {
		// given
		Long boardId = 1L;
		Long imageId = 10L;

		OwnerBoard board = OwnerBoard.builder()
			.title("제목")
			.content("내용")
			.user(mockUser)
			.build();

		OwnerBoardImage image1 = mock(OwnerBoardImage.class);
		ReflectionTestUtils.setField(image1, "id", imageId);
		OwnerBoardImage image2 = mock(OwnerBoardImage.class);

		ReflectionTestUtils.setField(board, "id", boardId);
		ReflectionTestUtils.setField(board, "images", new ArrayList<>(List.of(image1, image2)));

		given(ownerBoardRepository.findByIdWithImages(boardId)).willReturn(Optional.of(board));
		given(ownerBoardImageService.findImageById(imageId)).willReturn(image1);

		// when
		ownerBoardService.deleteImage(boardId, imageId);

		// then
		verify(ownerBoardRepository).findByIdWithImages(boardId);
		verify(ownerBoardImageService).findImageById(imageId);
		verify(ownerBoardImageService).deleteImageInternal(image1);
		assertEquals(1, board.getImages().size());
	}

}
