package com.study.petory.domain.ownerBoardComment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.domain.ownerBoard.entity.OwnerBoard;
import com.study.petory.domain.ownerBoard.service.OwnerBoardService;
import com.study.petory.domain.ownerBoardComment.dto.request.OwnerBoardCommentRequestDto;
import com.study.petory.domain.ownerBoardComment.dto.response.OwnerBoardCommentCreateResponseDto;
import com.study.petory.domain.ownerBoardComment.dto.response.OwnerBoardCommentGetResponseDto;
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

	// 주인커뮤니티 댓글 조회
	@Override
	@Transactional(readOnly = true)
	public Page<OwnerBoardCommentGetResponseDto> findAllOwnerBoardComments(int page) {

		int adjustedPage = (page > 0) ? page - 1 : 0;
		PageRequest pageRequest = PageRequest.of(adjustedPage, 10, Sort.by("createdAt").ascending());

		Page<OwnerBoardComment> comments = ownerBoardCommentRepository.findAll(pageRequest);

		return comments.map(OwnerBoardCommentGetResponseDto::from);
	}

}