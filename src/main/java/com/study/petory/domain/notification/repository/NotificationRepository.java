package com.study.petory.domain.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
