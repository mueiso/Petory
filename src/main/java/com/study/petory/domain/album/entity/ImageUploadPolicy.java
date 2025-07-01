package com.study.petory.domain.album.entity;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.UserRole;

@Component
public class ImageUploadPolicy {

	private final Map<Role, Integer> upload = Map.of(
		Role.ADMIN, 20,
		Role.USER, 2
	);

	// 권한을 통해 등록할 수 있는 최대 개수를 조회
	public int getMaxUploadCount(List<UserRole> roleList) {
		return roleList.stream()
			.map(role -> upload.getOrDefault(role.getRole(), 0))
			.max(Integer::compareTo)
			.orElse(0);
	}

	// 등록하는 이미지 수량 확인을 검증
	public boolean canUpload(List<UserRole> roleList, int currentCount) {
		return currentCount <= getMaxUploadCount(roleList);
	}
}
