package com.study.petory.domain.chat.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.study.petory.domain.chat.service.ChatService;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	ChatService chatService;

	@Test
	void 채팅방_생성_성공() throws Exception {
		//given
		Long tradeBoardId = 1L;

		//when
		mockMvc.perform(post("/{tradeBoardId}", tradeBoardId))
			.andExpect(status().isCreated()) //then
			.andExpect(jsonPath("$.tradeBoardId").value(equalTo(tradeBoardId)));
	}
}