package com.study.petory.domain.tradeBoard.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.tradeBoard.entity.TradeBoard;
import com.study.petory.domain.tradeBoard.entity.TradeCategory;
import com.study.petory.domain.user.entity.User;

public interface TradeBoardRepository extends JpaRepository<TradeBoard, Long> {

	Page<TradeBoard> findByUser(User user, Pageable pageable);
}
