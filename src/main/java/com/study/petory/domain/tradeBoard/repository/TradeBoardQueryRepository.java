package com.study.petory.domain.tradeBoard.repository;

import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Page;

import com.study.petory.domain.tradeBoard.entity.TradeBoard;
import com.study.petory.domain.tradeBoard.entity.TradeCategory;

public interface TradeBoardQueryRepository {

	Page<TradeBoard> findAll(TradeCategory category, Pageable pageable);

	Page<TradeBoard> findByUserId(Long userId, Pageable pageable);
}
