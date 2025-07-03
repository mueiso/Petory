package com.study.petory.domain.tradeboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.tradeboard.entity.TradeBoardImage;

public interface TradeBoardImageRepository extends JpaRepository<TradeBoardImage, Long> {
}
