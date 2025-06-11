package com.study.petory.domain.tradeBoard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.tradeBoard.entity.TradeBoardImage;

public interface TradeBoardImageRepository extends JpaRepository<TradeBoardImage, Long> {
}
