package com.study.petory.domain.place.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(controllers = PlaceController.class)
@WithMockUser(roles = {"USER", "ADMIN"})
public class PlaceControllerTest {
	//
	// @Autowired
	// private MockMvc mockMvc;
	//
	// @MockitoBean
	// PlaceService placeService;
	//
	// @MockitoBean
	// PlaceReviewService placeReviewService;
	//
	// @MockitoBean
	// PlaceReportService placeReportService;
	//
	// @MockitoBean
	// PlaceLikeService placeLikeService;
	//
	// @MockitoBean
	// private JwtProvider jwtProvider;
	//
	// @MockitoBean
	// private SecurityWhitelist securityWhitelist;
	//
	// @MockitoBean
	// private RateLimitFilter rateLimitFilter;
	//
	// @MockitoBean
	// private RedisTemplate<String, String> redisTemplate;
	//
	// @Test
	// @DisplayName("장소 등록")
	// void createPlace() throws Exception {
	//
	// 	PlaceCreateRequestDto requestDto = new PlaceCreateRequestDto("testName", null, PlaceType.CAFE, "testAddr",
	// 		BigDecimal.ZERO, BigDecimal.ZERO);
	//
	// 	Place place = Place.builder()
	// 		.placeName("testName")
	// 		.placeType(PlaceType.CAFE)
	// 		.address("testAddr")
	// 		.longitude(BigDecimal.ZERO)
	// 		.latitude(BigDecimal.ZERO)
	// 		.build();
	//
	// 	PlaceCreateResponseDto responseDto = PlaceCreateResponseDto.of(place, null);
	//
	// 	when(placeService.savePlace(anyLong(), any(PlaceCreateRequestDto.class), isNull())).thenReturn(responseDto);
	//
	// 	MockMultipartFile json = new MockMultipartFile(
	// 		"requestDto",
	// 		"",
	// 		"application/json",
	// 		new ObjectMapper().writeValueAsBytes(requestDto)
	// 	);
	//
	// 	mockMvc.perform(MockMvcRequestBuilders.multipart("/places")
	// 			.file(json)
	// 			.with(csrf())
	// 			.contentType(MediaType.MULTIPART_FORM_DATA)
	// 			.accept(MediaType.APPLICATION_JSON))
	// 		.andExpect(MockMvcResultMatchers.status().isOk())
	// 		.andExpect(MockMvcResultMatchers.jsonPath("$.data.placeName").value(equalTo("testName")));
	// }
}
