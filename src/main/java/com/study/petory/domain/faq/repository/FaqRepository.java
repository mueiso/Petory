package com.study.petory.domain.faq.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.faq.entity.Faq;

public interface FaqRepository extends JpaRepository<Faq, Long> {
}
