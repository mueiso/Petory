package com.study.petory.domain.ownerBoard.service;

import static org.assertj.core.api.Assertions.*;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardCreateRequestDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCreateResponseDto;
import com.study.petory.domain.ownerBoard.entity.OwnerBoard;
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
	void 유저소통_게시글과_이미지를_함께_저장에_성공다() {
		// given
		OwnerBoardCreateRequestDto dto = new OwnerBoardCreateRequestDto("제목", "내용");

		List<MultipartFile> images = List.of(
			new MockMultipartFile("image", "test.jpg", "image/jpeg", "image data".getBytes())
		);

		OwnerBoard mockBoard = OwnerBoard.builder().title("제목")
			.content("내용")
			.user(mockUser)
			.build();

		List<String> mockUrls = List.of("https://s3.url/test1.jpg", "https://s3.url/test2.jpg");

		given(userRepository.findById(1L)).willReturn(Optional.of(mockUser));
		given(ownerBoardRepository.save(any(OwnerBoard.class))).willReturn(mockBoard);
		given(ownerBoardImageService.uploadAndSaveAll(any(), any())).willReturn(mockUrls);

		// when
		OwnerBoardCreateResponseDto response = ownerBoardService.saveOwnerBoard(dto, images);

		// then
		assertThat(response.getTitle()).isEqualTo("제목");
		assertThat(response.getContent()).isEqualTo("내용");
		assertThat(response.getImageUrls()).containsExactlyElementsOf(mockUrls);
	}

	// 유저소통_게시글_저장에_성공한다.
	// 유저소통_게시글_검색어_없이_전체_조회에_성공한다
	// 유저소통_게시글_검색어_포함_전체_조회에_성공한다
	// 유저소통_게시글_단건_조회에_성공한다
	// 유저소통_게시글_수정에_성공한다
	// 유저소통_게시글_삭제에_성공한다
	// 유저소통_게시글_사진_삭제에_성공한다

}
