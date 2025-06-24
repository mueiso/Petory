package com.study.petory.domain.album;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.common.security.CustomPrincipal;
import com.study.petory.common.util.AbstractImageService;
import com.study.petory.domain.album.dto.request.AlbumCreateRequestDto;
import com.study.petory.domain.album.dto.request.AlbumUpdateRequestDto;
import com.study.petory.domain.album.dto.request.AlbumVisibilityUpdateRequestDto;
import com.study.petory.domain.album.dto.response.AlbumGetAllResponseDto;
import com.study.petory.domain.album.dto.response.AlbumGetOneResponseDto;
import com.study.petory.domain.album.entity.Album;
import com.study.petory.domain.album.entity.AlbumImage;
import com.study.petory.domain.album.entity.AlbumVisibility;
import com.study.petory.domain.album.repository.AlbumImageRepository;
import com.study.petory.domain.album.repository.AlbumRepository;
import com.study.petory.domain.album.service.AlbumImageServiceImpl;
import com.study.petory.domain.album.service.AlbumServiceImpl;
import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserPrivateInfo;
import com.study.petory.domain.user.entity.UserRole;
import com.study.petory.domain.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {

	@InjectMocks
	private AlbumServiceImpl albumService;

	@Mock
	private AlbumRepository albumRepository;

	@Mock
	private AlbumImageServiceImpl albumImageService;

	@Mock
	private AlbumImageRepository albumImageRepository;

	@Mock
	private UserService userService;

	@Mock
	private AbstractImageService abstractImageService;

	private final UserPrivateInfo testUserInfo = UserPrivateInfo.builder()
		.authId("1")
		.name("실명")
		.mobileNum("000-0000-0000")
		.build();
	List<UserRole> testUserRole = new ArrayList<>(List.of(new UserRole(Role.USER)));

	private final User testUser = User.builder()
		.email("testEmail@email.com")
		.nickname("별명")
		.userPrivateInfo(testUserInfo)
		.userRole(testUserRole)
		.build();


	private final MockMultipartFile mockMultipartFile = new MockMultipartFile(
			"testImages",
			"test.jpg",
			"image/jpeg",
			"image data".getBytes()
		);

	private final Album testAlbum = Album.builder()
		.user(testUser)
		.content("내용")
		.albumVisibility(AlbumVisibility.PUBLIC)
		.build();

	private final AlbumImage testAlbumImage = AlbumImage.builder()
		.album(testAlbum)
		.url("url")
		.build();

	private final List<MultipartFile> testImages = new ArrayList<>();

	private final List<AlbumImage> testAlbumImageList = new ArrayList<>();

	@BeforeEach
	public void set() {
		ReflectionTestUtils.setField(testUser, "id", 1L);
		ReflectionTestUtils.setField(testAlbum, "id", 1L);
	}

	public void setImageSize(int imageSize) {
		testImages.clear();
		testAlbumImageList.clear();

		for (int i = 0; i < imageSize; i++) {
			testImages.add(mockMultipartFile);
		}
		for (int i = 0; i < imageSize; i++) {
			testAlbumImageList.add(testAlbumImage);
		}
		ReflectionTestUtils.setField(testAlbum, "albumImageList", testAlbumImageList);
	}

	private Page<Album> setAlbumPage(int total, AlbumVisibility visibility, Pageable pageable) {
		List<Album> albumList = new ArrayList<>();
		for (int i = 0; i < total; i++) {
			ReflectionTestUtils.setField(testAlbum, "albumVisibility", visibility);
			albumList.add(testAlbum);
		}
		int start = (int)pageable.getOffset();
		int end = Math.min(start + pageable.getPageSize(), albumList.size());
		List<Album> startAndEnd = albumList.subList(start, end);
		return new PageImpl<>(startAndEnd, pageable, albumList.size());
	}

	@Test
	@DisplayName("유저가 앨범을 저장한다.")
	public void saveAlbum() {
		// given
		Long userId = 1L;
		int imageSize = 1;

		setImageSize(imageSize);

		given(albumImageService.findImageSize(testUser.getUserRole())).willReturn(imageSize);

		given(userService.findUserById(userId)).willReturn(testUser);

		AlbumCreateRequestDto requestDto = new AlbumCreateRequestDto("내용", null);

		// when
		albumService.saveAlbum(userId, requestDto, testImages);

		// then
		verify(albumRepository, times(1)).save(any(Album.class));

		// eq()로 검사하는 이유는 List<MultipartFile> 객체를 외부에서 만들어서 넣어주기 때문에 같은 값인지 확인
		verify(albumImageService, times(1)).uploadAndSaveAll(eq(testImages), any(Album.class));
	}

	@Test
	@DisplayName("앨범 전체를 조회한다.")
	public void findAllAlbum() {
		// given
		int imageSize = 3;

		setImageSize(imageSize);

		Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

		Page<Album> albumPage = setAlbumPage(12, AlbumVisibility.PUBLIC, pageable);

		given(albumRepository.findAllAlbum(true, null, pageable)).willReturn(albumPage);

		// when
		Page<AlbumGetAllResponseDto> response = albumService.findAllAlbum(true, null, pageable);

		// then
		assertThat(response.getContent()).hasSize(10);
		assertThat(response.getTotalElements()).isEqualTo(12);
		assertThat(response.getTotalPages()).isEqualTo(2);
		assertThat(response.isFirst()).isTrue();
	}

	@Test
	@DisplayName("사용자의 앨범 전체를 조회한다.")
	public void findAuthUserAllAlbum() {
		// given
		Long userId = 1L;
		int imageSize = 3;

		setImageSize(imageSize);

		Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

		Page<Album> albumPage = setAlbumPage(12, AlbumVisibility.PUBLIC, pageable);

		given(albumRepository.findAllAlbum(false, userId, pageable)).willReturn(albumPage);

		// when
		Page<AlbumGetAllResponseDto> response = albumService.findAllAlbum(false, userId, pageable);

		// then
		assertThat(response.getContent()).hasSize(10);
		assertThat(response.getTotalElements()).isEqualTo(12);
		assertThat(response.getTotalPages()).isEqualTo(2);
		assertThat(response.isFirst()).isTrue();
	}

	@Test
	@DisplayName("다른 유저의 앨범 전체를 조회한다.")
	public void findUserAllAlbum() {
		// given
		Long userId = 1L;
		int imageSize = 3;

		setImageSize(imageSize);

		Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

		Page<Album> albumPage = setAlbumPage(12, AlbumVisibility.PUBLIC, pageable);

		given(albumRepository.findAllAlbum(true, userId, pageable)).willReturn(albumPage);

		// when
		Page<AlbumGetAllResponseDto> response = albumService.findAllAlbum(true, userId, pageable);

		// then
		assertThat(response.getContent()).hasSize(10);
		assertThat(response.getTotalElements()).isEqualTo(12);
		assertThat(response.getTotalPages()).isEqualTo(2);
		assertThat(response.isFirst()).isTrue();
	}

	@Test
	@DisplayName("단일 앨범을 조회한다.")
	public void findOneAlbum() {
		// given
		Long userId = 1L;
		Long albumId = 1L;
		int imageSize = 3;

		setImageSize(imageSize);

		CustomPrincipal customUser = new CustomPrincipal(userId, "email.com", "별명", List.of());

		given(albumRepository.findOneAlbumByUser(true, albumId)).willReturn(Optional.of(testAlbum));
		given(albumRepository.isUserAlbum(userId, albumId)).willReturn(false);

		// when
		AlbumGetOneResponseDto response = albumService.findOneAlbum(customUser, albumId);

		// then
		assertThat(response.getAlbumId()).isEqualTo(1L);
		assertThat(response.getContent()).isEqualTo("내용");
		assertThat(response.getAlbumImageList()).hasSize(imageSize);
	}

	@Test
	@DisplayName("앨범을 수정한다.")
	public void updateAlbum() {
		// given
		Long userId = 1L;
		Long albumId = 1L;
		AlbumUpdateRequestDto request = new AlbumUpdateRequestDto("수정된 내용");

		given(albumRepository.findOneAlbumByUser(false, albumId)).willReturn(Optional.of(testAlbum));

		// when
		albumService.updateAlbum(userId, albumId, request);

		// then
		Album updatedAlbum = albumService.findAlbumByAlbumId(false, albumId);

		assertThat(updatedAlbum.getContent()).isEqualTo("수정된 내용");
	}

	@Test
	@DisplayName("앨범 공개 여부를 변경한다.")
	public void updateVisibility() {
		// given
		Long userId = 1L;
		Long albumId = 1L;
		AlbumVisibilityUpdateRequestDto request = new AlbumVisibilityUpdateRequestDto(AlbumVisibility.PRIVATE);

		given(albumRepository.findOneAlbumByUser(false, albumId)).willReturn(Optional.of(testAlbum));

		// when
		albumService.updateVisibility(userId, albumId, request);

		// then
		Album updatedAlbum = albumService.findAlbumByAlbumId(false, albumId);

		assertThat(updatedAlbum.getAlbumVisibility()).isEqualTo(AlbumVisibility.PRIVATE);
	}

	@Test
	@DisplayName("앨범을 삭제한다.")
	public void deleteAlbum() {
		// given
		Long userId = 1L;
		Long albumId = 1L;
		int imageSize = 3;

		setImageSize(imageSize);

		given(albumRepository.findOneAlbumByUser(false, albumId)).willReturn(Optional.of(testAlbum));

		// when
		albumService.deleteAlbum(userId, albumId);

		// then
		verify(albumRepository, times(1)).deleteById(albumId);
		verify(albumImageService, times(imageSize)).deleteImage(any());
	}

	@Test
	@DisplayName("앨범에 사진을 추가한다.")
	public void saveNewAlbumImage() {
		// given
		Long userId = 1L;
		Long albumId = 1L;
		int imageSize = 1;

		setImageSize(imageSize);

		given(albumImageService.findImageSize(testUser.getUserRole())).willReturn(imageSize);

		given(albumRepository.findOneAlbumByUser(false, albumId)).willReturn(Optional.of(testAlbum));
		given(userService.findUserById(userId)).willReturn(testUser);

		// when
		albumService.saveNewAlbumImage(userId, albumId, testImages);

		// then
		verify(albumImageService, times(1)).uploadAndSaveAll(testImages, testAlbum);
	}

	@Test
	@DisplayName("앨범의 사진을 삭제한다.")
	public void deleteAlbumImage() {
		// given
		Long userId = 1L;
		Long imageId = 1L;
		int imageSize = 3;

		setImageSize(imageSize);

		Album album = testAlbumImage.getAlbum();
		given(albumImageService.findImageById(imageId)).willReturn(testAlbumImage);
		given(albumRepository.findOneAlbumByUser(false, album.getId())).willReturn(Optional.of(testAlbum));


		// when
		albumService.deleteAlbumImage(userId, imageId);

		// then
		verify(albumImageService, times(1)).deleteImageInternal(any(AlbumImage.class));
		assertThat(album.getAlbumImageList()).hasSize(imageSize - 1);
	}
}
