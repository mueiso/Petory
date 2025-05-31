package com.study.petory.domain.ownerBoard.service;

import org.springframework.stereotype.Service;

import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardCreateRequestDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCreateResponseDto;
import com.study.petory.domain.ownerBoard.entity.OwnerBoard;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OwnerBoardServiceImpl implements OwnerBoardService {
	private final UserRepository userRepository;

	@Override
	public OwnerBoardCreateResponseDto saveOwnerBoard(OwnerBoardCreateRequestDto dto)
	{
		User user = userRepository.findById(1L).orElseThrow(); //todo 추후 토큰값으로 수정

		// OwnerBoard ownerBoard =

		return null;
	}
}
