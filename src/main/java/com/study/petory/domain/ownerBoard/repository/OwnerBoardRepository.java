package com.study.petory.domain.ownerBoard.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.study.petory.domain.ownerBoard.entity.OwnerBoard;

public interface OwnerBoardRepository extends JpaRepository<OwnerBoard, Long> {

	Page<OwnerBoard> findByTitleContaining(String title, PageRequest pageRequest);

	@Query(value = "SELECT * FROM tb_owner_board o WHERE o.id = :id", nativeQuery = true)
	Optional<OwnerBoard> findByIdIncludingDeleted(@Param("id") Long id);

	@Query("SELECT ob FROM OwnerBoard ob LEFT JOIN FETCH ob.images WHERE ob.id = :boardId")
	Optional<OwnerBoard> findByIdWithImages(@Param("boardId") Long boardId);
}
