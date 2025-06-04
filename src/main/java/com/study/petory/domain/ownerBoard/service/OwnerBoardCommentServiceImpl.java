package com.study.petory.domain.ownerBoard.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.domain.ownerBoard.entity.OwnerBoard;
import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardCommentCreateRequestDto;
import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardCommentUpdateRequestDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCommentCreateResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCommentGetResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCommentUpdateResponseDto;
import com.study.petory.domain.ownerBoard.entity.OwnerBoardComment;
import com.study.petory.domain.ownerBoard.repository.OwnerBoardCommentRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;
import com.study.petory.exception.CustomException;
import com.study.petory.exception.enums.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OwnerBoardCommentServiceImpl implements OwnerBoardCommentService {

	private final OwnerBoardCommentRepository ownerBoardCommentRepository;
	private final OwnerBoardService ownerBoardService;
	private final UserRepository userRepository;

	// CommentId로 OwnerBoardComment 조회
	@Override
	@Transactional(readOnly = true)
	public OwnerBoardComment findOwnerBoardCommentById(Long commentId) {
		return ownerBoardCommentRepository.findById(commentId)
			.orElseThrow(() -> new CustomException(ErrorCode.OWNER_BOARD_COMMENT_NOT_FOUND));
	}

	// 주인커뮤니티 댓글 생성
	@Override
	public OwnerBoardCommentCreateResponseDto saveOwnerBoardComment(Long boardId,
		OwnerBoardCommentCreateRequestDto dto) {

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
	public Page<OwnerBoardCommentGetResponseDto> findAllOwnerBoardComments(Long boardId, int page) {

		int adjustedPage = (page > 0) ? page - 1 : 0;
		PageRequest pageRequest = PageRequest.of(adjustedPage, 10, Sort.by("createdAt").ascending());

		Page<OwnerBoardComment> comments = ownerBoardCommentRepository.findByOwnerBoardId(boardId, pageRequest);

		return comments.map(OwnerBoardCommentGetResponseDto::from);
	}

	// 주인커뮤니티 댓글 수정
	@Override
	@Transactional
	public OwnerBoardCommentUpdateResponseDto updateOwnerBoardComment(Long boardId, Long commentId,
		OwnerBoardCommentUpdateRequestDto dto) {
		// 본인 댓글인지 검증 로직 추가 예정

		OwnerBoardComment comment = findOwnerBoardCommentById(commentId);

		if (boardId != comment.getOwnerBoard().getId()) {
			throw new CustomException(ErrorCode.OWNER_BOARD_COMMENT_MISMATCH);
		}

		if (dto.getContent() != null) {
			comment.updateContent(dto.getContent());
		}

		return OwnerBoardCommentUpdateResponseDto.from(comment);
	}

	// 주인커뮤니티 댓글 삭제
	@Override
	@Transactional
	public void deleteOwnerBoardComment(Long boardId, Long commentId) {
		// 본인 댓글인지 검증 로직 추가

		OwnerBoardComment comment = findOwnerBoardCommentById(commentId);

		if (boardId != comment.getOwnerBoard().getId()) {
			throw new CustomException(ErrorCode.OWNER_BOARD_COMMENT_MISMATCH);
		}

		comment.deactivateEntity();
	}

}