package com.study.petory.domain.tradeBoard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.tradeBoard.entity.TradeBoard;

public interface TradeBoardRepository extends JpaRepository<TradeBoard, Long> {
}
