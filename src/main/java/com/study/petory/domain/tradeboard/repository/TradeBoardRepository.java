package com.study.petory.domain.tradeboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.tradeboard.entity.TradeBoard;

public interface TradeBoardRepository extends JpaRepository<TradeBoard, Long>, TradeBoardQueryRepository {

}
