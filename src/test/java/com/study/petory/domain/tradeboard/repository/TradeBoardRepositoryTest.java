package com.study.petory.domain.tradeboard.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import com.study.petory.common.config.QueryDSLConfig;
import com.study.petory.domain.tradeboard.entity.TradeBoard;
import com.study.petory.domain.tradeboard.entity.TradeCategory;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QueryDSLConfig.class)
@ActiveProfiles("test")
class TradeBoardRepositoryTest {

	@Autowired
	private TradeBoardRepository tradeBoardRepository;

	@Autowired
	private UserRepository userRepository;

	private TradeBoard tradeBoard;

	private User user;

	@BeforeEach
	void setUp() {
		this.user = User.builder()
			.email("test@test.com")
			.nickname("nickname")
			.build();

		this.tradeBoard = TradeBoard.builder()
			.user(user)
			.category(TradeCategory.TOYS)
			.title("title")
			.content("content")
			.price(5000)
			.build();

	}

	@Test
	void 거래게시판_전체조회_성공() {
		//given
		PageRequest pageable = PageRequest.of(0, 10);

		userRepository.save(user);
		tradeBoardRepository.save(tradeBoard);

		//when
		Page<TradeBoard> findedBoard = tradeBoardRepository.findAll((TradeCategory)null, pageable);

		//then
		assertThat(findedBoard).hasSize(1);
		assertThat(findedBoard.getContent().get(0).getUser()).isEqualTo(user);
		assertThat(findedBoard.getContent().get(0).getCategory()).isEqualTo(tradeBoard.getCategory());
		assertThat(findedBoard.getContent().get(0).getTitle()).isEqualTo(tradeBoard.getTitle());
		assertThat(findedBoard.getContent().get(0).getContent()).isEqualTo(tradeBoard.getContent());
		assertThat(findedBoard.getContent().get(0).getPrice()).isEqualTo(tradeBoard.getPrice());
	}

	@Test
	void 카테고리로_전체조회_성공() {
		//given
		PageRequest pageable = PageRequest.of(0, 10);

		userRepository.save(user);
		tradeBoardRepository.save(tradeBoard);

		//when
		Page<TradeBoard> findedBoard1 = tradeBoardRepository.findAll(TradeCategory.TOYS, pageable);
		Page<TradeBoard> findedBoard2 = tradeBoardRepository.findAll(TradeCategory.HEALTH, pageable);

		//then
		assertThat(findedBoard1).hasSize(1);
		assertThat(findedBoard1.getContent().get(0).getUser()).isEqualTo(user);
		assertThat(findedBoard1.getContent().get(0).getCategory()).isEqualTo(tradeBoard.getCategory());
		assertThat(findedBoard1.getContent().get(0).getTitle()).isEqualTo(tradeBoard.getTitle());
		assertThat(findedBoard1.getContent().get(0).getContent()).isEqualTo(tradeBoard.getContent());
		assertThat(findedBoard1.getContent().get(0).getPrice()).isEqualTo(tradeBoard.getPrice());

		assertThat(findedBoard2).hasSize(0);
	}

	@Test
	void 유저별_게시글_조회_성공() {
		//given
		PageRequest pageable = PageRequest.of(0, 10);

		userRepository.save(user);
		tradeBoardRepository.save(tradeBoard);

		//when
		Page<TradeBoard> findedBoard = tradeBoardRepository.findByUserId(user.getId(), pageable);

		//then
		assertThat(findedBoard).hasSize(1);
		assertThat(findedBoard.getContent().get(0).getUser()).isEqualTo(user);
		assertThat(findedBoard.getContent().get(0).getTitle()).isEqualTo(tradeBoard.getTitle());
		assertThat(findedBoard.getContent().get(0).getContent()).isEqualTo(tradeBoard.getContent());
		assertThat(findedBoard.getContent().get(0).getPrice()).isEqualTo(tradeBoard.getPrice());
	}
}