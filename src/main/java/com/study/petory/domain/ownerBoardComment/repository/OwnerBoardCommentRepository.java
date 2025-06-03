package com.study.petory.domain.ownerBoardComment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.ownerBoardComment.entity.OwnerBoardComment;

public interface OwnerBoardCommentRepository extends JpaRepository<OwnerBoardComment, Long> {

	List<OwnerBoardComment> findTop10ByOwnerBoardIdOrderByCreatedAt(Long boardId);
}
