package com.study.petory.domain.chat.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import com.study.petory.common.entity.BaseEntityWithCreatedAt;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Document(collection = "chatroom")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom extends BaseEntityWithCreatedAt {

	@Id
	private String id;

	private Long sellerId; // 판매자 닉네임

	private Long customerId; // 구매자 닉네임

	private Long tradeBoardId; //판매글 Id

	private String lastMessageId;
}
