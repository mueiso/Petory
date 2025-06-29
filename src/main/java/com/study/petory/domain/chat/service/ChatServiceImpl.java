package com.study.petory.domain.chat.service;

import java.net.URL;
import java.time.Duration;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.study.petory.common.util.S3Uploader;
import com.study.petory.domain.chat.dto.request.MessageSendRequestDto;
import com.study.petory.domain.chat.dto.request.PresignedUrlRequestDto;
import com.study.petory.domain.chat.dto.response.ChatRoomCreateResponseDto;
import com.study.petory.domain.chat.dto.response.ChatRoomGetAllResponseDto;
import com.study.petory.domain.chat.dto.response.ChatRoomGetResponseDto;
import com.study.petory.domain.chat.dto.response.PresignedUrlResponseDto;
import com.study.petory.domain.chat.entity.ChatMessage;
import com.study.petory.domain.chat.entity.ChatRoom;
import com.study.petory.domain.chat.repository.ChatRepository;
import com.study.petory.domain.tradeBoard.entity.TradeBoard;
import com.study.petory.domain.tradeBoard.repository.TradeBoardRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;
import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{

	@Value("${spring.cloud.aws.region.static}")
	private String region;

	@Value("${cloud.aws.bucket}")
	private String bucket;

	private final ChatRepository chatRepository;
	private final UserRepository userRepository;
	private final TradeBoardRepository tradeBoardRepository;
	private final S3Presigner s3Presigner;
	private final S3Uploader s3Uploader;

	//사용하지 않으면 삭제 예정
	public User findUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}

	public ChatRoom findChatRoom(String chatRoomId) {
		return chatRepository.findById(new ObjectId(chatRoomId))
			.orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
	}

	//메시지 보내기
	@Override
	public ChatMessage createMessage(Long userId, MessageSendRequestDto requestDto) {

		ChatRoom chatRoom = findChatRoom(requestDto.getChatRoomId());
		User user = findUser(userId);

		if (!chatRoom.isMember(user.getId())) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		ChatMessage message = ChatMessage.builder()
			.senderId(user.getId())
			.senderNickname(user.getNickname())
			.messageType(requestDto.getMessageType())
			.content(requestDto.getContent())
			.build();

		chatRoom.addMessage(message);
		chatRepository.save(chatRoom);

		return message;
	}

	//presignedUrl 생성
	@Override
	public PresignedUrlResponseDto createPresignedUrl(PresignedUrlRequestDto requestDto) {

		// 파일 확장자 추출
		s3Uploader.getExtension(requestDto.getFilename());

		// 업로드될 S3 객체의 Key 경로 생성
		String objectKey = "chat/" + requestDto.getChatRoomId() + "/" + requestDto.getFilename();

		// 업로드 객체 생성
		PutObjectRequest objectRequest = PutObjectRequest.builder()
			.bucket(bucket)
			.key(objectKey)
			.contentType(requestDto.getContentType())
			.build();

		// Presigned Url 생성을 위한 요청 객체 생성
		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
			.signatureDuration(Duration.ofMinutes(5))
			.putObjectRequest(objectRequest)
			.build();

		// Presigned URL 생성
		URL uploadUrl = s3Presigner.presignPutObject(presignRequest).url();

		// 업로드 완료 후 접근 가능한 정적 파일 URL 생성
		String fileUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + objectKey;

		return new PresignedUrlResponseDto(uploadUrl.toString(), fileUrl);
	}

	//채팅방 생성
	@Override
	public ChatRoomCreateResponseDto saveChatRoom(Long userId, Long tradeBoardId) {

		TradeBoard tradeBoard = tradeBoardRepository.findById(tradeBoardId)
			.orElseThrow(() -> new CustomException(ErrorCode.TRADE_BOARD_NOT_FOUND));

		findUser(userId);

		if (tradeBoard.isOwner(userId)) {
			throw new CustomException(ErrorCode.CANNOT_SEND_MESSAGE_TO_SELF);
		}

		ChatRoom chatRoom = ChatRoom.builder()
			.tradeBoardId(tradeBoardId)
			.sellerId(tradeBoard.getUserId())
			.customerId(userId)
			.build();

		chatRepository.save(chatRoom);

		return new ChatRoomCreateResponseDto(chatRoom);
	}

	//채팅방 전체 조회
	@Override
	public List<ChatRoomGetAllResponseDto> findAllChatRoom(Long userId, Pageable pageable) {

		List<ChatRoom> chatRooms = chatRepository.findChatRoomsByUserId(userId, pageable);

		return chatRooms.stream()
			.map(chatRoom -> new ChatRoomGetAllResponseDto(chatRoom, userId))
			.toList();
	}

	//채팅방 단건 조회
	@Override
	public ChatRoomGetResponseDto findChatRoomById(Long userId, String chatRoomId) {

		ChatRoom chatRoom = findChatRoom(chatRoomId);

		if (!chatRoom.isMember(userId)) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		return new ChatRoomGetResponseDto(chatRoom);
	}

	//채팅방 나가기
	@Override
	public void leaveChatRoomById(Long userId, String chatRoomId) {

		ChatRoom chatRoom = findChatRoom(chatRoomId);

		if (!chatRoom.isMember(userId)) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		chatRoom.leaveChatRoom(userId);
		chatRepository.save(chatRoom);
	}

}
