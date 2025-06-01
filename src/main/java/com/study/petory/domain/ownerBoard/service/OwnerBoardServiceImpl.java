package com.study.petory.domain.ownerBoard.service;

import org.springframework.stereotype.Service;

import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardCreateRequestDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCreateResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardGetResponseDto;
import com.study.petory.domain.ownerBoard.entity.OwnerBoard;
import com.study.petory.domain.ownerBoard.repository.OwnerBoardRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;
import com.study.petory.exception.CustomException;
import com.study.petory.exception.enums.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OwnerBoardServiceImpl implements OwnerBoardService {
	private final OwnerBoardRepository ownerBoardRepository;
	private final UserRepository userRepository;

	// ownerBoardId로 OwnerBoard 조회
	@Override
	public OwnerBoard findOwnerBoardById(Long boardId) {
		return ownerBoardRepository.findById(boardId).orElseThrow(() -> new CustomException(ErrorCode.NO_RESOURCE));
	}

	// 게시글 생성
	@Override
	public OwnerBoardCreateResponseDto saveOwnerBoard(OwnerBoardCreateRequestDto dto) {
		User user = userRepository.findById(1L).orElseThrow(); // 추후 토큰값으로 수정

		OwnerBoard ownerBoard = OwnerBoard.builder()
			.title(dto.getTitle())
			.content(dto.getContent())
			.user(user)
			.build();

		ownerBoardRepository.save(ownerBoard);

		return OwnerBoardCreateResponseDto.from(ownerBoard);
	}

	// 게시글 단건 조회
	@Override
	public OwnerBoardGetResponseDto findOwnerBoard(Long boardId) {
		OwnerBoard ownerBoard = findOwnerBoardById(boardId);

		return OwnerBoardGetResponseDto.from(ownerBoard);
	}
}
