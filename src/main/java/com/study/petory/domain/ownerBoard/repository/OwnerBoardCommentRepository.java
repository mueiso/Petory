package com.study.petory.domain.ownerBoard.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.ownerBoard.entity.OwnerBoard;
import com.study.petory.domain.ownerBoard.entity.OwnerBoardComment;
import com.study.petory.domain.user.entity.User;

public interface OwnerBoardCommentRepository extends JpaRepository<OwnerBoardComment, Long> {

	List<OwnerBoardComment> findTop10ByOwnerBoardIdOrderByCreatedAt(Long boardId);

	Page<OwnerBoardComment> findByOwnerBoardId(Long boardId, Pageable pageable);

	List<OwnerBoardComment> findByUser(User user);
}
