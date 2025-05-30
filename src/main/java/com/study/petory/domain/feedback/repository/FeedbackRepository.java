package com.study.petory.domain.feedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.feedback.entity.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
