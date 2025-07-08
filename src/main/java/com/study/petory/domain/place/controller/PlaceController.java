package com.study.petory.domain.place.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.common.exception.enums.SuccessCode;
import com.study.petory.common.response.CommonResponse;
import com.study.petory.common.security.CustomPrincipal;
import com.study.petory.domain.place.dto.request.PlaceCreateRequestDto;
import com.study.petory.domain.place.dto.request.PlaceReportCancelRequestDto;
import com.study.petory.domain.place.dto.request.PlaceReportRequestDto;
import com.study.petory.domain.place.dto.request.PlaceReviewCreateRequestDto;
import com.study.petory.domain.place.dto.request.PlaceReviewUpdateRequestDto;
import com.study.petory.domain.place.dto.request.PlaceStatusChangeRequestDto;
import com.study.petory.domain.place.dto.request.PlaceUpdateRequestDto;
import com.study.petory.domain.place.dto.response.PlaceCreateResponseDto;
import com.study.petory.domain.place.dto.response.PlaceGetAllResponseDto;
import com.study.petory.domain.place.dto.response.PlaceGetResponseDto;
import com.study.petory.domain.place.dto.response.PlaceLikeResponseDto;
import com.study.petory.domain.place.dto.response.PlaceReviewCreateResponseDto;
import com.study.petory.domain.place.dto.response.PlaceReviewUpdateResponseDto;
import com.study.petory.domain.place.dto.response.PlaceUpdateResponseDto;
import com.study.petory.domain.place.entity.PlaceType;
import com.study.petory.domain.place.service.PlaceLikeService;
import com.study.petory.domain.place.service.PlaceReportService;
import com.study.petory.domain.place.service.PlaceReviewService;
import com.study.petory.domain.place.service.PlaceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/places")
public class PlaceController {

	private final PlaceService placeService;
	private final PlaceReviewService placeReviewService;
	private final PlaceReportService placeReportService;
	private final PlaceLikeService placeLikeService;

	/**
	 * 장소 등록
	 * @param currentUser login user 정보
	 * @param requestDto 장소 등록에 필요한 정보
	 * @param images image
	 * @return CommonResponse 방식의 등록된 장소 정보
	 */
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<CommonResponse<PlaceCreateResponseDto>> createPlace(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@Valid @RequestPart PlaceCreateRequestDto requestDto,
		@RequestPart(required = false) List<MultipartFile> images
	) {
		return CommonResponse.of(SuccessCode.CREATED, placeService.savePlace(currentUser.getId(), requestDto, images));
	}

	/**
	 * 전체 장소 조회
	 * 페이징 관련 스크롤 방식이 옳을 지에 대한 고민
	 * @param placeName 장소 이름 일부 입력 시에도 조회 가능
	 * @param placeType 장소 타입 입력 시 타입 조건 기준 조회 가능
	 * @param pageable 기본 페이징 설정. 한 페이지에 10개의 게시글(장소) 조회
	 * @return CommonResponse 방식의 페이징된 장소 정보 목록
	 */
	@GetMapping
	public ResponseEntity<CommonResponse<Page<PlaceGetAllResponseDto>>> getAllPlace(
		@RequestParam(required = false) String placeName,
		@RequestParam(required = false) String placeType,
		@RequestParam(required = false) String address,
		@PageableDefault Pageable pageable
	) {
		PlaceType type = placeService.parsePlaceType(placeType);
		return CommonResponse.of(SuccessCode.FOUND, placeService.findAllPlace(placeName, type, address, pageable));
	}

	/**
	 * 특정 장소 조회
	 * 사진, 평균 평점, 페이징 된 리뷰 목록 추가 예정
	 * @param placeId 장소 식별자
	 * @return CommonResponse 방식의 특정 장소 정보 + 해당 장소의 리뷰 목록
	 */
	@GetMapping("/{placeId}")
	public ResponseEntity<CommonResponse<PlaceGetResponseDto>> getByPlaceId(
		@PathVariable Long placeId
	) {
		return CommonResponse.of(SuccessCode.FOUND, placeService.findByPlaceId(placeId));
	}

	/**
	 * 장소 수정
	 * @param placeId 장소 식별자
	 * @param requestDto 장소 수정에 필요한 정보
	 * @return CommonResponse 방식의 특정 장소의 수정된 정보
	 */
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{placeId}")
	public ResponseEntity<CommonResponse<PlaceUpdateResponseDto>> updatePlace(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long placeId,
		@Valid @RequestBody PlaceUpdateRequestDto requestDto
	) {
		return CommonResponse.of(SuccessCode.UPDATED,
			placeService.updatePlace(currentUser.getId(), placeId, requestDto));
	}

	/**
	 * 삭제된 장소 복구
	 * soft delete 된 장소를 다시 복구하는 기능
	 * 삭제된 장소인지 검증 로직 필요
	 * @param placeId 장소 식별자
	 * @param requestDto 장소 복구에 필요한 정보
	 * @return CommonResponse 방식의 삭제된 장소 복구 메시지
	 */
	@PreAuthorize("hasRole('ADMIN')")
	@PatchMapping("/{placeId}/restore")
	public ResponseEntity<CommonResponse<Void>> restorePlace(
		@PathVariable Long placeId,
		@Valid @RequestBody PlaceStatusChangeRequestDto requestDto
	) {
		placeService.restorePlace(placeId, requestDto);
		return CommonResponse.of(SuccessCode.RESTORED);
	}

	/**
	 * 장소 삭제
	 * soft delete 구현
	 * 삭제 가능한 장소인지 검증 로직 필요
	 * @param placeId 장소 식별자
	 * @param requestDto 장소 삭제에 필요한 Enum 정보
	 * @return CommonResponse 방식의 장소 삭제 메시지
	 */
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{placeId}")
	public ResponseEntity<CommonResponse<Void>> deletePlace(
		@PathVariable Long placeId,
		@Valid @RequestBody PlaceStatusChangeRequestDto requestDto
	) {
		placeService.deletePlace(placeId, requestDto);
		return CommonResponse.of(SuccessCode.DELETED);
	}

	/**
	 * 리뷰 등록
	 * 유저 부분은 추후 수정 예정
	 * @param placeId 장소 식별자
	 * @param requestDto 리뷰 등록에 필요한 정보
	 * @return CommonResponse 방식의 등록된 리뷰에 대한 정보
	 */
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@PostMapping("/{placeId}/reviews")
	public ResponseEntity<CommonResponse<PlaceReviewCreateResponseDto>> createPlaceReview(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long placeId,
		@Valid @RequestBody PlaceReviewCreateRequestDto requestDto
	) {
		return CommonResponse.of(SuccessCode.CREATED,
			placeReviewService.savePlaceReview(currentUser.getId(), placeId, requestDto));
	}

	/**
	 * 리뷰 수정
	 * @param placeId 장소 식별자
	 * @param reviewId 리뷰 식별자
	 * @param requestDto 리뷰 등록에 필요한 정보
	 * @return CommonResponse 방식의 수정된 리뷰에 대한 정보
	 */
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@PutMapping("/{placeId}/reviews/{reviewId}")
	public ResponseEntity<CommonResponse<PlaceReviewUpdateResponseDto>> updatePlaceReview(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long placeId,
		@PathVariable Long reviewId,
		@Valid @RequestBody PlaceReviewUpdateRequestDto requestDto
	) {
		return CommonResponse.of(SuccessCode.UPDATED,
			placeReviewService.updatePlaceReview(currentUser.getId(), placeId, reviewId, requestDto));
	}

	/**
	 * 삭제된 리뷰 복구
	 * soft delete 된 리뷰를 다시 복구하는 기능
	 * @param placeId 장소 식별자
	 * @param reviewId 리뷰 식별자
	 * @return CommonResponse 방식의 삭제된 리뷰 복구 메시지
	 */
	@PreAuthorize("hasRole('ADMIN')")
	@PatchMapping("/{placeId}/reviews/{reviewId}/restore")
	public ResponseEntity<CommonResponse<Void>> restorePlaceReview(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long placeId,
		@PathVariable Long reviewId
	) {
		placeReviewService.restorePlaceReview(currentUser.getId(), placeId, reviewId);
		return CommonResponse.of(SuccessCode.RESTORED);
	}

	/**
	 * 리뷰 삭제
	 * soft delete 구현
	 * @param placeId 장소 식별자
	 * @param reviewId 리뷰 식별자
	 * @return CommonResponse 방식의 리뷰 삭제 메시지
	 */
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@DeleteMapping("/{placeId}/reviews/{reviewId}")
	public ResponseEntity<CommonResponse<Void>> deletePlaceReview(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long placeId,
		@PathVariable Long reviewId
	) {
		placeReviewService.deletePlaceReview(currentUser.getId(), placeId, reviewId);
		return CommonResponse.of(SuccessCode.DELETED);
	}

	/**
	 * 장소 신고
	 * @param currentUser login user 정보
	 * @param placeId 장소 식별자
	 * @param requestDto 장소 신고에 필요한 정보
	 * @return CommonResponse 방식의 신고 완료 메시지
	 */
	@PreAuthorize("hasRole('USER')")
	@PostMapping("/{placeId}/reports")
	public ResponseEntity<CommonResponse<String>> reportPlace(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long placeId,
		@Valid @RequestBody PlaceReportRequestDto requestDto
	) {
		placeReportService.reportPlace(currentUser.getId(), placeId, requestDto);
		return CommonResponse.of(SuccessCode.CREATED, "신고가 완료되었습니다.");
	}

	/**
	 * 장소 신고 취소
	 * @param currentUser login user 정보(관리자 정보)
	 * @param placeId 장소 식별자
	 * @param reportId 신고 식별자
	 * @param requestDto 장소 신고 취소에 필요한 정보
	 * @return CommonResponse 방식의 신고 취소 메세지
	 */
	@PreAuthorize("hasRole('ADMIN')")
	@PatchMapping("/{placeId}/reports/{reportId}")
	public ResponseEntity<CommonResponse<String>> cancelReportPlace(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long placeId,
		@PathVariable Long reportId,
		@Valid @RequestBody PlaceReportCancelRequestDto requestDto
	) {
		placeReportService.cancelReportPlace(currentUser.getId(), placeId, reportId, requestDto);
		return CommonResponse.of(SuccessCode.UPDATED, "신고 취소가 완료되었습니다.");
	}

	/**
	 * 장소 좋아요
	 * @param currentUser login user 정보
	 * @param placeId 장소 식별자
	 * @return CommonResponse 방식의 좋아요에 대한 정보
	 */
	@PreAuthorize("hasRole('USER')")
	@PostMapping("/{placeId}/like")
	public ResponseEntity<CommonResponse<PlaceLikeResponseDto>> likePlace(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long placeId
	) {
		return CommonResponse.of(SuccessCode.CREATED, placeLikeService.likePlace(currentUser.getId(), placeId));
	}

	/**
	 * 장소 인기 랭킹
	 * @param placeType 장소 타입 입력 시 타입 조건 기준 조회 가능
	 * @return CommonResponse 방식의 인기 랭킹 목록 출력
	 */
	@GetMapping("/rank")
	public ResponseEntity<CommonResponse<List<PlaceGetAllResponseDto>>> getPlaceRank(
		@RequestParam(required = false) String placeType
	) {
		PlaceType type = placeService.parsePlaceType(placeType);
		return CommonResponse.of(SuccessCode.FOUND, placeService.findPlaceRank(type));
	}

	/**
	 * 장소 사진 추가
	 * @param currentUser login user 정보
	 * @param placeId 장소 식별자
	 * @param images 이미지
	 * @return CommonResponse 방식의 생성 성공 메세지
	 */
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping(value = "/{placeId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<CommonResponse<Void>> addImages(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long placeId,
		@RequestPart List<MultipartFile> images
	) {
		placeService.addImages(currentUser.getId(), placeId, images);

		return CommonResponse.of(SuccessCode.CREATED);
	}

	/**
	 * 장소 사진 삭제
	 * @param currentUser login user 정보
	 * @param placeId 장소 식별자
	 * @param imageId 이미지
	 * @return CommonResponse 방식의 삭제 성공 메세지
	 */
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{placeId}/images/{imageId}")
	public ResponseEntity<CommonResponse<Void>> deleteImage(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long placeId,
		@PathVariable Long imageId
	) {
		placeService.deleteImage(currentUser.getId(), placeId, imageId);

		return CommonResponse.of(SuccessCode.DELETED);
	}
}
