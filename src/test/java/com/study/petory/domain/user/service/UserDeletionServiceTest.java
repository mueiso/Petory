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

		/* [given]
		 * 삭제 대상 User 를 Mock 객체로 생성
		 */
		User user = mock(User.class);

		// OwnerBoard 레포지토리에서 해당 유저의 게시글 목록을 반환하도록 설정
		OwnerBoard board = mock(OwnerBoard.class);
		when(ownerBoardRepository.findByUser(user)).thenReturn(List.of(board));

		// OwnerBoardComment 레포지토리에서 해당 유저의 댓글 목록 반환
		OwnerBoardComment comment = mock(OwnerBoardComment.class);
		when(ownerBoardCommentRepository.findByUser(user)).thenReturn(List.of(comment));

		// 장소 레포지토리에서 유저가 등록한 장소 목록 반환
		Place place = mock(Place.class);
		when(placeRepository.findByUser(user)).thenReturn(List.of(place));

		// 좋아요 레포지토리에서 유저가 남긴 좋아요 목록 반환
		PlaceLike like = mock(PlaceLike.class);
		when(placeLikeRepository.findByUser(user)).thenReturn(List.of(like));

		// 신고 레포지토리에서 유저가 작성한 신고 목록 반환
		PlaceReport report = mock(PlaceReport.class);
		when(placeReportRepository.findByUser(user)).thenReturn(List.of(report));

		// 리뷰 레포지토리에서 유저가 작성한 리뷰 목록 반환
		PlaceReview review = mock(PlaceReview.class);
		when(placeReviewRepository.findByUser(user)).thenReturn(List.of(review));

		/* [when]
		 * 테스트 대상 메서드 호출 (연관 데이터 참조 제거 후 유저 삭제)
		 */
		userDeletionService.deleteUser(user);

		/* [then]
		 * 각 연관 엔티티에서 setUser(null) 호출되었는지 검증 (참조 끊기)
		 */
		verify(board).setUser(null);
		verify(comment).setUser(null);
		verify(place).setUser(null);
		verify(like).setUser(null);
		verify(report).setUser(null);
		verify(review).setUser(null);

		// 유저 삭제 되었는지 검증
		verify(userRepository).delete(user);
	}
}
