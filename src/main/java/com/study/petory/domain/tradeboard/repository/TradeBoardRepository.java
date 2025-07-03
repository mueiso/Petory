package com.study.petory.domain.tradeboard.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.tradeboard.entity.TradeBoard;
import com.study.petory.domain.user.entity.User;

public interface TradeBoardRepository extends JpaRepository<TradeBoard, Long>, TradeBoardQueryRepository {

	Page<TradeBoard> findByUser(User user, Pageable pageable);
}
