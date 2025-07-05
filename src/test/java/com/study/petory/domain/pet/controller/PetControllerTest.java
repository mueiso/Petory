// package com.study.petory.domain.pet.controller;
//
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import java.nio.charset.StandardCharsets;
// import java.time.LocalDate;
// import java.util.List;
//
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.mock.web.MockMultipartFile;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.web.servlet.MockMvc;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.study.petory.domain.pet.dto.PetCreateRequestDto;
// import com.study.petory.domain.pet.dto.PetUpdateRequestDto;
// import com.study.petory.domain.pet.entity.PetSize;
//
// @SpringBootTest
// @AutoConfigureMockMvc
// class PetControllerIntegrationTest {
//
// 	@Autowired
// 	private MockMvc mockMvc;
//
// 	@Autowired
// 	private ObjectMapper objectMapper;
//
// 	@Test
// 	@WithMockUser(username = "1", roles = "USER")
// 	void createPet_성공() throws Exception {
//
// 		// given
// 		PetCreateRequestDto requestDto = new PetCreateRequestDto(
// 			"멍멍이",
// 			PetSize.SMALL,
// 			"푸들",
// 			"MALE",
// 			"2020-05-20"
// 		);
//
// 		MockMultipartFile requestPart = new MockMultipartFile(
// 			"requestDto", "requestDto", "application/json",
// 			objectMapper.writeValueAsBytes(requestDto)
// 		);
//
// 		MockMultipartFile image = new MockMultipartFile(
// 			"images", "dog.png", MediaType.IMAGE_PNG_VALUE,
// 			"fake image".getBytes(StandardCharsets.UTF_8)
// 		);
//
// 		// when & then
// 		mockMvc.perform(multipart("/pets")
// 				.file(requestPart)
// 				.file(image)
// 				.contentType(MediaType.MULTIPART_FORM_DATA))
// 			.andExpect(status().isCreated())
// 			.andExpect(jsonPath("$.code").value("CREATED"))
// 			.andExpect(jsonPath("$.data").doesNotExist());
// 	}
//
// 	@Test
// 	@WithMockUser(username = "1", roles = "USER")
// 	void getMyPets_성공() throws Exception {
//
// 		mockMvc.perform(get("/pets"))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.code").value("FOUND"));
// 	}
//
// 	@Test
// 	@WithMockUser(username = "1", roles = "USER")
// 	void getPet_성공() throws Exception {
//
// 		// 사전 조건: DB에 ID = 1인 pet 이 존재해야 함
// 		mockMvc.perform(get("/pets/{petId}", 1L))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.code").value("FOUND"))
// 			.andExpect(jsonPath("$.data.id").value(1L));
// 	}
//
// 	@Test
// 	@WithMockUser(username = "1", roles = "USER")
// 	void updatePet_성공() throws Exception {
//
// 		PetUpdateRequestDto updateDto = new PetUpdateRequestDto(
// 			"강아지",
// 			"FEMALE",
// 			"2021, 1, 1"
// 		);
//
// 		MockMultipartFile updateRequest = new MockMultipartFile(
// 			"requestDto", "requestDto", "application/json",
// 			objectMapper.writeValueAsBytes(updateDto)
// 		);
//
// 		mockMvc.perform(multipart("/pets/{petId}", 1L)
// 				.file(updateRequest)
// 				.with(request -> {
// 					request.setMethod("PUT");  // multipart는 기본 POST이므로 변경 필요
// 					return request;
// 				}))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.code").value("UPDATED"))
// 			.andExpect(jsonPath("$.data.name").value("강아지"));
// 	}
//
// 	@Test
// 	@WithMockUser(username = "1", roles = "USER")
// 	void deletePet_성공() throws Exception {
//
// 		mockMvc.perform(delete("/pets/{petId}", 1L))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.code").value("DELETED"));
// 	}
//
// 	@Test
// 	@WithMockUser(username = "1", roles = "USER")
// 	void restorePet_성공() throws Exception {
//
// 		mockMvc.perform(patch("/pets/{petId}/restore", 1L))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.code").value("RESTORED"));
// 	}
//
// 	@Test
// 	@WithMockUser(username = "1", roles = "USER")
// 	void deletePetImage_성공() throws Exception {
//
// 		mockMvc.perform(delete("/pets/images/{petImageId}", 1L))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.code").value("DELETED"));
// 	}
// }
