package com.study.petory.domain.ownerboard.repository;

import static com.study.petory.domain.user.entity.UserStatus.*;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.study.petory.common.config.QueryDSLConfig;
import com.study.petory.domain.ownerboard.dto.response.OwnerBoardGetAllResponseDto;
import com.study.petory.domain.ownerboard.entity.OwnerBoard;
import com.study.petory.domain.ownerboard.entity.OwnerBoardImage;
import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserPrivateInfo;
import com.study.petory.domain.user.entity.UserRole;
import com.study.petory.domain.user.entity.UserStatus;
import com.study.petory.domain.user.repository.UserRepository;

@DataJpaTest
@Import(QueryDSLConfig.class)
public class OwnerBoardQueryRepositoryImplTest {

	@Autowired
	OwnerBoardRepository ownerBoardRepository;

	@Autowired
	OwnerBoardImageRepository ownerBoardImageRepository;

	@Autowired
	UserRepository userRepository;
	@Autowired
	private OwnerBoardQueryRepositoryImpl ownerBoardQueryRepositoryImpl;

	@Test
	void 조건없이_게시글_전체_조회한다() {
		// given
		User user = createUserWithStatus(ACTIVE);
		createAndSaveOwnerBoard(user, "제목1", "내용1");
		createAndSaveOwnerBoard(user, "제목2", "내용2");
		createAndSaveOwnerBoard(user, "제목3", "내용3");

		Pageable pageable = PageRequest.of(0, 2);

		// when
		Page<OwnerBoardGetAllResponseDto> result =
			ownerBoardQueryRepositoryImpl.findAllWithFirstImageAndTitleOptional(null, pageable);

		// then
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getTotalElements()).isEqualTo(3);
		assertThat(result.getTotalPages()).isEqualTo(2);
		assertThat(result.getNumber()).isEqualTo(0);
	}

	@Test
	void 제목으로_게시글_검색_조회한다() {
		// given
		User user = createUserWithStatus(ACTIVE);
		createAndSaveOwnerBoard(user, "강아지 산책", "내용1");
		createAndSaveOwnerBoard(user, "강아지 낮잠", "내용2");
		createAndSaveOwnerBoard(user, "고양이 낮잠", "내용3");

		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<OwnerBoardGetAllResponseDto> result
			= ownerBoardQueryRepositoryImpl.findAllWithFirstImageAndTitleOptional(
			"강아지", pageable);

		// then
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getContent())
			.extracting(OwnerBoardGetAllResponseDto::getTitle)
			.containsExactlyInAnyOrder("강아지 산책", "강아지 낮잠");
	}

	@Test
	void 첫번째_이미지만_조회한다() {
		// given
		User user = createUserWithStatus(ACTIVE);
		OwnerBoard ownerBoard = createAndSaveOwnerBoard(user, "게시글", "내용");

		OwnerBoardImage image1 = new OwnerBoardImage("https://image1.jpg", ownerBoard);
		OwnerBoardImage image2 = new OwnerBoardImage("https://image2.jpg", ownerBoard);
		OwnerBoardImage image3 = new OwnerBoardImage("https://image3.jpg", ownerBoard);

		ownerBoard.addImage(image1);
		ownerBoard.addImage(image2);
		ownerBoard.addImage(image3);

		ownerBoardImageRepository.saveAll(List.of(image1, image2, image3));

		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<OwnerBoardGetAllResponseDto> result = ownerBoardQueryRepositoryImpl.findAllWithFirstImageAndTitleOptional(
			null, pageable);

		// then
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getImageUrl()).isNotNull();
		assertThat(result.getContent().get(0).getImageUrl()).isEqualTo("https://image1.jpg");
	}

	@Test
	void 게시글과_이미지를_함께_단건_조회한다() {
		// given
		User user = createUserWithStatus(ACTIVE);

		OwnerBoard ownerBoard = OwnerBoard.builder()
			.title("사진 게시글")
			.content("내용")
			.user(user)
			.build();
		ownerBoardRepository.save(ownerBoard);

		OwnerBoardImage image1 = new OwnerBoardImage("https://test1.jpg", ownerBoard);
		OwnerBoardImage image2 = new OwnerBoardImage("https://test2.jpg", ownerBoard);

		ownerBoard.addImage(image1);
		ownerBoard.addImage(image2);

		ownerBoardImageRepository.saveAll(List.of(image1, image2));

		// when
		Optional<OwnerBoard> result = ownerBoardRepository.findByIdWithImages(ownerBoard.getId());

		// then
		assertThat(result).isPresent();
		assertThat(result.get().getImages()).hasSize(2);
		assertThat(result.get().getImages().get(0).getUrl()).startsWith("https://");
	}

	@Test
	void 삭제되지_않은_게시글을_조회한다() {
		// given
		User user = createUserWithStatus(ACTIVE);

		OwnerBoard ownerBoard = OwnerBoard.builder()
			.user(user)
			.title("제목")
			.content("내용")
			.build();

		ownerBoardRepository.save(ownerBoard);
		assertThat(ownerBoard.getId()).isNotNull();

		// when
		Optional<OwnerBoard> result = ownerBoardQueryRepositoryImpl.findByIdIncludingDeleted(ownerBoard.getId());

		// then
		assertThat(result).isPresent();
		assertThat(result.get().getId()).isEqualTo(ownerBoard.getId());
		assertThat(result.get().getDeletedAt()).isNull();
	}

	@Test
	void 삭제된_게시글을_단건_조회한다() {
		// given
		User user = createUserWithStatus(ACTIVE);

		OwnerBoard ownerBoard = OwnerBoard.builder()
			.user(user)
			.title("제목")
			.content("내용")
			.build();

		ownerBoardRepository.save(ownerBoard);

		//게시글 삭제(soft delete)
		ownerBoard.deactivateEntity();
		ownerBoardRepository.saveAndFlush(ownerBoard);

		// when
		Optional<OwnerBoard> result = ownerBoardQueryRepositoryImpl.findByIdIncludingDeleted(ownerBoard.getId());

		// then
		assertThat(result).isPresent();
		assertThat(result.get().getId()).isEqualTo(ownerBoard.getId());
		assertThat(result.get().getDeletedAt()).isNotNull();
	}

	// 테스트용 유저 객체를 생성하는 헬퍼 메서드
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

		userRepository.save(user);

		return user;
	}

	// 게시글을 생성하는 헬퍼 메서드
	private OwnerBoard createAndSaveOwnerBoard(User user, String title, String content) {
		OwnerBoard board = OwnerBoard.builder()
			.user(user)
			.title(title)
			.content(content)
			.build();
		return ownerBoardRepository.save(board);
	}

}
