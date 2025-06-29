package com.study.petory.domain.ownerBoard.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardCreateRequestDto;
import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardUpdateRequestDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCommentGetResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCreateResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardGetAllResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardGetResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardUpdateResponseDto;
import com.study.petory.domain.ownerBoard.entity.OwnerBoard;
import com.study.petory.domain.ownerBoard.entity.OwnerBoardComment;
import com.study.petory.domain.ownerBoard.entity.OwnerBoardImage;
import com.study.petory.domain.ownerBoard.repository.OwnerBoardCommentRepository;
import com.study.petory.domain.ownerBoard.repository.OwnerBoardRepository;
import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.service.UserServiceImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OwnerBoardServiceImpl implements OwnerBoardService {
	private final OwnerBoardRepository ownerBoardRepository;
	private final OwnerBoardCommentRepository ownerBoardCommentRepository;
	private final OwnerBoardImageService ownerBoardImageService;
	private final UserServiceImpl userService;

	/**
	 * 게시글 작성자 검증 메서드
	 * 이 메서드는 OwnerBoardService 내부에서만 사용됩니다.
	 */
	private void validBoardOwnerShip(OwnerBoard ownerBoard, Long userId, ErrorCode errorCode) {
		if (!ownerBoard.isEqualUser(userId)) {
			throw new CustomException(errorCode);
		}
	}

	// ownerBoardId로 OwnerBoard 조회
	@Override
	public OwnerBoard findOwnerBoardById(Long boardId) {
		return ownerBoardRepository.findByIdWithImages(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.NO_RESOURCE));
	}

	// 게시글 생성
	@Override
	@Transactional
	public OwnerBoardCreateResponseDto saveOwnerBoard(Long userId, OwnerBoardCreateRequestDto dto,
		List<MultipartFile> images) {

		User user = userService.findUserById(userId);

		OwnerBoard ownerBoard = OwnerBoard.builder()
			.title(dto.getTitle())
			.content(dto.getContent())
			.user(user)
			.build();

		ownerBoardRepository.save(ownerBoard);

		List<String> urls = new ArrayList<>();

		if (images != null && !images.isEmpty()) {
			urls = ownerBoardImageService.uploadAndSaveAll(images, ownerBoard);
		}

		return OwnerBoardCreateResponseDto.of(ownerBoard, urls);
	}

	// 게시글 전체 조회
	@Override
	public Page<OwnerBoardGetAllResponseDto> findAllOwnerBoards(String title, Pageable pageable) {

		Page<OwnerBoard> boards;
		if (title != null) {
			boards = ownerBoardRepository.findByTitleContaining(title, pageable);
		} else {
			boards = ownerBoardRepository.findAllWithImages(pageable);
		}

		return boards.map(OwnerBoardGetAllResponseDto::from);
	}

	// 게시글 단건 조회
	@Override
	public OwnerBoardGetResponseDto findOwnerBoard(Long boardId) {
		OwnerBoard ownerBoard = findOwnerBoardById(boardId);

		List<OwnerBoardComment> initialComments = ownerBoardCommentRepository.findTop10ByOwnerBoardIdOrderByCreatedAt(
			boardId);

		List<OwnerBoardCommentGetResponseDto> commentsList = initialComments.stream()
			.map(OwnerBoardCommentGetResponseDto::from)
			.toList();

		return OwnerBoardGetResponseDto.of(ownerBoard, commentsList);
	}

	// 게시글 수정
	@Override
	@Transactional
	public OwnerBoardUpdateResponseDto updateOwnerBoard(Long userId, Long boardId,
		OwnerBoardUpdateRequestDto requestDto) {

		OwnerBoard ownerBoard = findOwnerBoardById(boardId);

		validBoardOwnerShip(ownerBoard, userId, ErrorCode.ONLY_AUTHOR_CAN_EDIT);

		ownerBoard.updateOwnerBoard(requestDto.getTitle(), requestDto.getContent());

		return OwnerBoardUpdateResponseDto.from(ownerBoard);
	}

	// 게시글 삭제
	@Override
	@Transactional
	public void deleteOwnerBoard(Long userId, Long boardId) {

		OwnerBoard ownerBoard = findOwnerBoardById(boardId);

		User user = userService.findUserById(userId);

		if (!user.hasRole(Role.ADMIN)) {
			validBoardOwnerShip(ownerBoard, userId, ErrorCode.ONLY_AUTHOR_CAN_DELETE);
		}

		// 이미지 모두 hard delete(S3, DB)
		List<OwnerBoardImage> images = ownerBoard.getImages();

		for (OwnerBoardImage image : new ArrayList<>(images)) {
			ownerBoardImageService.deleteImage(image); // S3 이미지 정보 삭제
			ownerBoard.getImages().remove(image); // DB 이미지 정보 삭제, 연관관계를 끊어 고아객체로 만들면 delete 쿼리 발생
		}

		// 게시글 soft delete
		ownerBoard.deactivateEntity();
	}

	// 게시글 복구
	@Override
	@Transactional
	public void restoreOwnerBoard(Long userId, Long boardId) {

		OwnerBoard ownerBoard = ownerBoardRepository.findByIdIncludingDeleted(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.NO_RESOURCE));

		if (ownerBoard.getDeletedAt() == null) {
			throw new CustomException(ErrorCode.OWNER_BOARD_NOT_DELETED);
		}

		ownerBoard.restoreEntity();
	}

	// 게시글 사진 추가
	@Override
	@Transactional
	public void addImages(Long userId, Long boardId, List<MultipartFile> images) {
		OwnerBoard ownerBoard = findOwnerBoardById(boardId);

		validBoardOwnerShip(ownerBoard, userId, ErrorCode.ONLY_AUTHOR_CAN_EDIT);

		List<OwnerBoardImage> imageEntities = ownerBoardImageService.uploadAndReturnEntities(images, ownerBoard);
		for (OwnerBoardImage image : imageEntities) {
			ownerBoard.addImage(image);
		}
	}

	// 게시글 사진 삭제
	@Override
	@Transactional
	public void deleteImage(Long userId, Long boardId, Long imageId) {
		OwnerBoard ownerBoard = findOwnerBoardById(boardId);

		validBoardOwnerShip(ownerBoard, userId, ErrorCode.ONLY_AUTHOR_CAN_DELETE);

		OwnerBoardImage image = ownerBoardImageService.findImageById(imageId);

		if (!ownerBoard.isEqualId(image.getOwnerBoard().getId())) {
			throw new CustomException(ErrorCode.INVALID_INPUT);
		}

		ownerBoardImageService.deleteImageInternal(image);
		ownerBoard.getImages().remove(image);
	}
}
