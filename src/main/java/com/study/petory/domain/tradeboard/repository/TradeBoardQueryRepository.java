package com.study.petory.domain.tradeboard.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.study.petory.domain.tradeboard.entity.TradeBoard;
import com.study.petory.domain.tradeboard.entity.TradeCategory;

public interface TradeBoardQueryRepository {

	Page<TradeBoard> findAll(TradeCategory category, Pageable pageable);

	Page<TradeBoard> findByUserId(Long userId, Pageable pageable);
}
