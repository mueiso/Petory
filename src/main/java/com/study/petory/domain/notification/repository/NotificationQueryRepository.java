package com.study.petory.domain.notification.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.study.petory.domain.notification.entity.Notification;

public interface NotificationQueryRepository {

	Page<Notification> findByUserId(Long userId, Pageable pageable);

}
