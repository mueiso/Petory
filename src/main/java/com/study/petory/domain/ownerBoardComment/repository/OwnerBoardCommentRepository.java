package com.study.petory.domain.ownerBoardComment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.ownerBoardComment.entity.OwnerBoardComment;

public interface OwnerBoardCommentRepository extends JpaRepository<OwnerBoardComment, Long> {
}
