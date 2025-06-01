package com.study.petory.domain.ownerBoard.dto.request;

import com.study.petory.domain.ownerBoard.entity.OwnerBoard;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class OwnerBoardUpdateRequestDto {

	@Size(max = 30, message = "최대 30자까지 입력할 수 있습니다.")
	private String title;

	@Column(columnDefinition = "TEXT")
	private String content;

	public void update(OwnerBoard ownerBoard) {
		if(title != null) {ownerBoard.updateTitle(title);}
		if(content != null) {ownerBoard.updateContent(content);}
	}

}
