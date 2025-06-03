package com.study.petory.domain.ownerBoardComment.service;

import org.springframework.stereotype.Service;

import com.study.petory.domain.ownerBoard.entity.OwnerBoard;
import com.study.petory.domain.ownerBoard.repository.OwnerBoardRepository;
import com.study.petory.domain.ownerBoard.service.OwnerBoardService;
import com.study.petory.domain.ownerBoardComment.dto.request.OwnerBoardCommentRequestDto;
import com.study.petory.domain.ownerBoardComment.dto.response.OwnerBoardCommentCreateResponseDto;
import com.study.petory.domain.ownerBoardComment.entity.OwnerBoardComment;
import com.study.petory.domain.ownerBoardComment.repository.OwnerBoardCommentRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OwnerBoardCommentServiceImpl implements OwnerBoardCommentService {

	private final OwnerBoardCommentRepository ownerBoardCommentRepository;
	private final OwnerBoardService ownerBoardService;
	private final UserRepository userRepository;

	// 주인커뮤니티 댓글 생성
	@Override
	public OwnerBoardCommentCreateResponseDto saveOwnerBoardComment(Long boardId, OwnerBoardCommentRequestDto dto) {

		User user = userRepository.findById(1L).orElseThrow(); // 임시로 유저 생성, 추후 로그인유저 변경 예정
		OwnerBoard ownerBoard = ownerBoardService.findOwnerBoardById(boardId);

		OwnerBoardComment comment = OwnerBoardComment.builder()
			.user(user)
			.ownerBoard(ownerBoard)
			.content(dto.getContent())
			.build();

		ownerBoardCommentRepository.save(comment);

		return OwnerBoardCommentCreateResponseDto.from(comment);
	}
}