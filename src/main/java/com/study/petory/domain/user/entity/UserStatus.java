package com.study.petory.domain.user.entity;

import lombok.Getter;

@Getter
public enum UserStatus {
	ACTIVE,       // 정상 상태
	DEACTIVATED,  // 휴면 상태
	SUSPENDED,    // 관리자에 의해 비활성화된 상태
	DELETED       // hard delete 되기 전 soft delete 된 상태
}
