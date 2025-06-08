package com.study.petory.domain.tradeBoard.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.tradeBoard.entity.TradeBoard;
import com.study.petory.domain.tradeBoard.entity.TradeCategory;

public interface TradeBoardRepository extends JpaRepository<TradeBoard, Long> {

	Page<TradeBoard> findAllByCategory(TradeCategory category, PageRequest pageable);

}
