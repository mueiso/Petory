package com.study.petory.domain.ownerBoard.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.ownerBoard.entity.OwnerBoardComment;

public interface OwnerBoardCommentRepository extends JpaRepository<OwnerBoardComment, Long> {

	List<OwnerBoardComment> findTop10ByOwnerBoardIdOrderByCreatedAt(Long boardId);

	Page<OwnerBoardComment> findByOwnerBoardId(Long boardId, Pageable pageable);
}
