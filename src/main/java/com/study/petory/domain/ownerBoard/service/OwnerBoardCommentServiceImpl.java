package com.study.petory.domain.ownerBoard.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardCommentCreateRequestDto;
import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardCommentUpdateRequestDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCommentCreateResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCommentGetResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCommentUpdateResponseDto;
import com.study.petory.domain.ownerBoard.entity.OwnerBoard;
import com.study.petory.domain.ownerBoard.entity.OwnerBoardComment;
import com.study.petory.domain.ownerBoard.repository.OwnerBoardCommentRepository;
import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.service.UserServiceImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OwnerBoardCommentServiceImpl implements OwnerBoardCommentService {

	private final OwnerBoardCommentRepository ownerBoardCommentRepository;
	private final OwnerBoardService ownerBoardService;
	private final UserServiceImpl userService;

	/**
	 * 댓글 작성자 검증 메서드
	 * 이 메서드는 OwnerBoardCommentService 내부에서만 사용됩니다.
	 */
	private void validBoardOwnerShip(OwnerBoardComment comment, Long userId, ErrorCode errorCode) {
		if (!comment.isEqualUser(userId)) {
			throw new CustomException(errorCode);
		}
	}

	// CommentId로 OwnerBoardComment 조회
	@Override
	public OwnerBoardComment findOwnerBoardCommentById(Long commentId) {
		return ownerBoardCommentRepository.findById(commentId)
			.orElseThrow(() -> new CustomException(ErrorCode.OWNER_BOARD_COMMENT_NOT_FOUND));
	}

	// 주인커뮤니티 댓글 생성
	@Override
	public OwnerBoardCommentCreateResponseDto saveOwnerBoardComment(Long userId, Long boardId,
		OwnerBoardCommentCreateRequestDto dto) {

		User user = userService.findUserById(userId);

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
	public Page<OwnerBoardCommentGetResponseDto> findAllOwnerBoardComments(Long boardId, Pageable pageable) {

		Page<OwnerBoardComment> comments = ownerBoardCommentRepository.findByOwnerBoardId(boardId, pageable);

		return comments.map(OwnerBoardCommentGetResponseDto::from);
	}

	// 주인커뮤니티 댓글 수정
	@Override
	@Transactional
	public OwnerBoardCommentUpdateResponseDto updateOwnerBoardComment(Long userId, Long boardId, Long commentId,
		OwnerBoardCommentUpdateRequestDto dto) {

		OwnerBoardComment comment = findOwnerBoardCommentById(commentId);

		validBoardOwnerShip(comment, userId, ErrorCode.ONLY_AUTHOR_CAN_EDIT);

		if (!comment.isEqualOwnerBoard(boardId)) {
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
	public void deleteOwnerBoardComment(Long userId, Long boardId, Long commentId) {

		OwnerBoardComment comment = findOwnerBoardCommentById(commentId);

		User user = userService.findUserById(userId);

		if (!user.hasRole(Role.ADMIN)) {
			validBoardOwnerShip(comment, userId, ErrorCode.ONLY_AUTHOR_CAN_DELETE);
		}

		if (!comment.isEqualOwnerBoard(boardId)) {
			throw new CustomException(ErrorCode.OWNER_BOARD_COMMENT_MISMATCH);
		}

		comment.deactivateEntity();
	}

}