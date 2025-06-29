package com.study.petory.domain.user.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.domain.ownerBoard.entity.OwnerBoard;
import com.study.petory.domain.ownerBoard.entity.OwnerBoardComment;
import com.study.petory.domain.ownerBoard.repository.OwnerBoardCommentRepository;
import com.study.petory.domain.ownerBoard.repository.OwnerBoardRepository;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.repository.PlaceRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDeletionService {

	private final UserRepository userRepository;
	private final OwnerBoardRepository ownerBoardRepository;
	private final OwnerBoardCommentRepository ownerBoardCommentRepository;
	private final PlaceRepository placeRepository;

	@Transactional
	public void deleteUser(User user) {

		// 1. OwnerBoard 의 user 참조 끊기
		List<OwnerBoard> boards = ownerBoardRepository.findByUser(user);
		for (OwnerBoard board : boards) {
			board.setUser(null);
		}

		// 2. OwnerBoardComment 의 user 참조 끊기
		List<OwnerBoardComment> comments = ownerBoardCommentRepository.findByUser(user);
		for (OwnerBoardComment comment : comments) {
			comment.setUser(null);
		}

		// 3. Place 의 user 참조 끊기
		List<Place> places = placeRepository.findByUser(user);
		for (Place place : places) {
			place.setUser(null);
		}

		// 4. User 실제 삭제
		userRepository.delete(user);
		log.info("[삭제] 유저 및 연관 데이터 정리 완료 - userId: {}, email: {}", user.getId(), user.getEmail());
	}
}
