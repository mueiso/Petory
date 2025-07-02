package com.study.petory.domain.tradeBoard.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.domain.tradeBoard.dto.request.TradeBoardCreateRequestDto;
import com.study.petory.domain.tradeBoard.dto.request.TradeBoardUpdateRequestDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardCreateResponseDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardGetAllResponseDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardGetResponseDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardUpdateResponseDto;
import com.study.petory.domain.tradeBoard.entity.TradeBoard;
import com.study.petory.domain.tradeBoard.entity.TradeBoardStatus;
import com.study.petory.domain.tradeBoard.entity.TradeCategory;
import com.study.petory.domain.tradeBoard.repository.TradeBoardQueryRepository;
import com.study.petory.domain.tradeBoard.repository.TradeBoardRepository;
import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserPrivateInfo;
import com.study.petory.domain.user.entity.UserRole;
import com.study.petory.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class TradeBoardServiceTest {

	@Mock
	private TradeBoardRepository tradeBoardRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private TradeBoardQueryRepository tradeBoardQueryRepository;

	@InjectMocks
	private TradeBoardServiceImpl tradeBoardService;

	@Mock
	private TradeBoardImageService tradeBoardImageService;


	private User user;
	private TradeBoard tradeBoard1;
	private TradeBoard tradeBoard2;

	@BeforeEach
	void setUp() {
		List<UserRole> userRoles = new ArrayList<>();
		userRoles.add(new UserRole(Role.USER));
		UserPrivateInfo userInfo = new UserPrivateInfo("authId", "name", "01012341234");
		this.user = new User("nickname", "test@test.com", userInfo, userRoles);
		ReflectionTestUtils.setField(user, "id", 1L);

		tradeBoard1 = TradeBoard.builder()
			.category(TradeCategory.TOYS)
			.title("title1")
			.content("content1")
			.price(1000)
			.user(user)
			.build();

		tradeBoard2 = TradeBoard.builder()
			.category(TradeCategory.HEALTH)
			.title("title2")
			.content("content2")
			.price(2000)
			.user(user)
			.build();
	}

	@Test
	void 거래_게시글_생성에_성공한다() {
		//given
		Long userId = 1L;

		TradeBoardCreateRequestDto requestDto =
			new TradeBoardCreateRequestDto(TradeCategory.TOYS, "title", "content", 5000);

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		TradeBoard tradeBoard = TradeBoard.builder()
			.category(requestDto.getCategory())
			.title(requestDto.getTitle())
			.content(requestDto.getContent())
			.price(requestDto.getPrice())
			.user(user)
			.build();

		when(tradeBoardRepository.save(any(TradeBoard.class))).thenReturn(tradeBoard);
		List<MultipartFile> images = null;

		//when
		TradeBoardCreateResponseDto responseDto = tradeBoardService.saveTradeBoard(userId, requestDto, images);

		//then
		assertThat(requestDto.getCategory()).isEqualTo(responseDto.getCategory());
		assertThat(requestDto.getTitle()).isEqualTo(responseDto.getTitle());
		assertThat(requestDto.getContent()).isEqualTo(responseDto.getContent());
		assertThat(responseDto.getImageUrls()).isEmpty();
		assertThat(requestDto.getPrice()).isEqualTo(responseDto.getPrice());
	}

	@Test
	void 카테고리_없이_전체_조회에_성공한다() {
		//given
		Pageable pageable = PageRequest.of(0, 10);

		List<TradeBoard> tradeBoards = List.of(tradeBoard1, tradeBoard2);
		Page<TradeBoard> page = new PageImpl<>(tradeBoards, pageable, tradeBoards.size());

		when(tradeBoardRepository.findAll(nullable(TradeCategory.class), eq(pageable))).thenReturn(page);

		// when
		Page<TradeBoardGetAllResponseDto> responseDto = tradeBoardService.findAllTradeBoard(null, pageable);

		// then
		assertThat(responseDto).isNotEmpty();
		assertThat(responseDto.getContent()).hasSize(2);
	}

	@Test
	void 카테고리로_전체_조회에_성공한다() {
		//given
		Pageable pageable = PageRequest.of(0, 10);

		List<TradeBoard> tradeBoards = List.of(tradeBoard1);
		Page<TradeBoard> page = new PageImpl<>(tradeBoards, pageable, tradeBoards.size());

		when(tradeBoardRepository.findAll(TradeCategory.TOYS, pageable)).thenReturn(page);

		// when
		Page<TradeBoardGetAllResponseDto> responseDto = tradeBoardService.findAllTradeBoard(TradeCategory.TOYS, pageable);

		// then
		assertThat(responseDto).isNotEmpty();
		assertThat(responseDto.getContent()).hasSize(1);
	}

	@Test
	void 게시글_단건_조회에_성공한다() {
		//given
		when(tradeBoardRepository.findById(1L)).thenReturn(Optional.ofNullable(tradeBoard1));

		//when
		TradeBoardGetResponseDto responseDto = tradeBoardService.findByTradeBoardId(1L);

		//then
		assertThat(responseDto.getCategory()).isEqualTo(tradeBoard1.getCategory());
		assertThat(responseDto.getTitle()).isEqualTo(tradeBoard1.getTitle());
		assertThat(responseDto.getContent()).isEqualTo(tradeBoard1.getContent());
		assertThat(responseDto.getPrice()).isEqualTo(tradeBoard1.getPrice());
		assertThat(responseDto.getSellerId()).isEqualTo(tradeBoard1.getUser().getId());
	}

	@Test
	void 유저별_게시글_조회에_성공한다() {
		// given
		Pageable pageable = PageRequest.of(0, 10);

		List<TradeBoard> tradeBoards = List.of(tradeBoard1, tradeBoard2);
		Page<TradeBoard> page = new PageImpl<>(tradeBoards, pageable, tradeBoards.size());

		when(tradeBoardRepository.findByUserId(user.getId(), pageable)).thenReturn(page);

		// when
		Page<TradeBoardGetAllResponseDto> responseDto = tradeBoardService.findByUser(user.getId(), pageable);

		// then
		assertThat(responseDto).isNotEmpty();
		assertThat(responseDto.getNumberOfElements()).isEqualTo(2);
	}

	@Test
	void 게시글_수정에_성공한다() {
		//given
		Long userId = 1L;

		TradeBoardUpdateRequestDto requestDto =
			new TradeBoardUpdateRequestDto(TradeCategory.TOYS, "title", "content", 5000, TradeBoardStatus.FOR_SALE);

		Long tradeBoardId = 1L;

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(tradeBoardRepository.findById(tradeBoardId)).thenReturn(Optional.ofNullable(tradeBoard1));

		//when
		TradeBoardUpdateResponseDto responseDto = tradeBoardService.updateTradeBoard(userId, tradeBoardId, requestDto);

		//then
		assertThat(responseDto.getCategory()).isEqualTo(requestDto.getCategory());
		assertThat(responseDto.getTitle()).isEqualTo(requestDto.getTitle());
		assertThat(responseDto.getContent()).isEqualTo(requestDto.getContent());
		assertThat(responseDto.getPrice()).isEqualTo(requestDto.getPrice());
		assertThat(responseDto.getStatus()).isEqualTo(requestDto.getStatus());
	}

	@Test
	void 게시글_상태_수정에_성공한다() {
		//given
		Long userId = 1L;
		TradeBoardStatus status = TradeBoardStatus.HIDDEN;
		Long tradeBoardId = 1L;

		when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
		when(tradeBoardRepository.findById(tradeBoardId)).thenReturn(Optional.ofNullable(tradeBoard1));

		//when
		tradeBoardService.updateTradeBoardStatus(userId, tradeBoardId, status);

		//then
		assertThat(tradeBoard1.getStatus()).isEqualTo(status);
	}
}