package com.study.petory.domain.user.service;

import static org.mockito.Mockito.*;

import java.util.List;

import com.study.petory.domain.ownerBoard.entity.OwnerBoard;
import com.study.petory.domain.ownerBoard.entity.OwnerBoardComment;
import com.study.petory.domain.ownerBoard.repository.OwnerBoardCommentRepository;
import com.study.petory.domain.ownerBoard.repository.OwnerBoardRepository;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceLike;
import com.study.petory.domain.place.entity.PlaceReport;
import com.study.petory.domain.place.entity.PlaceReview;
import com.study.petory.domain.place.repository.PlaceLikeRepository;
import com.study.petory.domain.place.repository.PlaceReportRepository;
import com.study.petory.domain.place.repository.PlaceRepository;
import com.study.petory.domain.place.repository.PlaceReviewRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserDeletionServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private OwnerBoardRepository ownerBoardRepository;

	@Mock
	private OwnerBoardCommentRepository ownerBoardCommentRepository;

	@Mock
	private PlaceRepository placeRepository;

	@Mock
	private PlaceLikeRepository placeLikeRepository;

	@Mock
	private PlaceReportRepository placeReportRepository;

	@Mock
	private PlaceReviewRepository placeReviewRepository;

	@InjectMocks
	private UserDeletionService userDeletionService;

	@Test
	void deleteUser_모든연관데이터_userNull설정후_User삭제() {

		// given
		User user = mock(User.class);

		// 각 연관 엔티티들 생성 및 mocking
		OwnerBoard board = mock(OwnerBoard.class);
		when(ownerBoardRepository.findByUser(user)).thenReturn(List.of(board));

		OwnerBoardComment comment = mock(OwnerBoardComment.class);
		when(ownerBoardCommentRepository.findByUser(user)).thenReturn(List.of(comment));

		Place place = mock(Place.class);
		when(placeRepository.findByUser(user)).thenReturn(List.of(place));

		PlaceLike like = mock(PlaceLike.class);
		when(placeLikeRepository.findByUser(user)).thenReturn(List.of(like));

		PlaceReport report = mock(PlaceReport.class);
		when(placeReportRepository.findByUser(user)).thenReturn(List.of(report));

		PlaceReview review = mock(PlaceReview.class);
		when(placeReviewRepository.findByUser(user)).thenReturn(List.of(review));

		// when
		userDeletionService.deleteUser(user);

		// then - 각 연관 엔티티에서 setUser(null) 호출되었는지 검증
		verify(board).setUser(null);
		verify(comment).setUser(null);
		verify(place).setUser(null);
		verify(like).setUser(null);
		verify(report).setUser(null);
		verify(review).setUser(null);

		// 유저 삭제 메서드가 호출되었는지 검증
		verify(userRepository).delete(user);
	}
}
