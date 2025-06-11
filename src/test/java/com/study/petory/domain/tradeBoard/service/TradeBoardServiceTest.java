package com.study.petory.domain.tradeBoard.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.domain.tradeBoard.dto.request.TradeBoardCreateRequestDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardCreateResponseDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardGetAllResponseDto;
import com.study.petory.domain.tradeBoard.entity.TradeBoard;
import com.study.petory.domain.tradeBoard.entity.TradeCategory;
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

	@InjectMocks
	private TradeBoardServiceImpl tradeBoardService;

	private User user;

	@BeforeEach
	void SerUser() {
		List<UserRole> userRoles = new ArrayList<>();
		userRoles.add(new UserRole(Role.USER));
		UserPrivateInfo userInfo = new UserPrivateInfo(1L, "name", "01012341234");
		this.user = new User("nickname", "test@test.com", userInfo, userRoles);
		ReflectionTestUtils.setField(user, "id", 1L);
	}

	@Test
	void 거래_게시글_생성에_성공한다() {
		//given
		TradeBoardCreateRequestDto requestDto =
			new TradeBoardCreateRequestDto(TradeCategory.TOYS, "title", "content", null, 5000);

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		TradeBoard tradeBoard = TradeBoard.builder()
			.category(requestDto.getCategory())
			.title(requestDto.getTitle())
			.content(requestDto.getContent())
			.price(requestDto.getPrice())
			.user(user)
			.build();

		when(tradeBoardRepository.save(any(TradeBoard.class))).thenReturn(tradeBoard);

		//when
		//TradeBoardCreateResponseDto responseDto = tradeBoardService.saveTradeBoard(requestDto, List<MultipartFile> images);

		//then
		// assertThat(requestDto.getCategory()).isEqualTo(responseDto.getCategory());
		// assertThat(requestDto.getTitle()).isEqualTo(responseDto.getTitle());
		// assertThat(requestDto.getContent()).isEqualTo(responseDto.getContent());
		// assertThat(requestDto.getPhotoUrl()).isNull();
		// assertThat(requestDto.getPrice()).isEqualTo(responseDto.getPrice());
	}

	@Test
	void 카테고리_없이_전체_조회에_성공한다() {
		//given
		int page = 1;
		PageRequest pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

		TradeBoard tradeBoard1 = TradeBoard.builder()
			.category(TradeCategory.TOYS)
			.title("title1")
			.content("content1")
			.price(1000)
			.user(user)
			.build();

		TradeBoard tradeBoard2 = TradeBoard.builder()
			.category(TradeCategory.HEALTH)
			.title("title2")
			.content("content2")
			.price(2000)
			.user(user)
			.build();

		tradeBoardRepository.save(tradeBoard1);
		tradeBoardRepository.save(tradeBoard2);

		//when

		//then

	}
}