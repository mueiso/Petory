package com.study.petory.domain.user.repository;

import static com.study.petory.domain.user.entity.QUser.*;
import static com.study.petory.domain.user.entity.QUserRole.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.petory.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<User> findByEmailWithUserRole(String email) {

		return Optional.ofNullable(
			queryFactory
				.selectFrom(user)
				.leftJoin(user.userRole, userRole).fetchJoin()
				.where(user.email.eq(email))
				.fetchOne()
		);
	}

	@Override
	public Optional<User> findByIdWithUserRole(Long id) {

		return Optional.ofNullable(
			queryFactory
				.selectFrom(user)
				.leftJoin(user.userRole, userRole).fetchJoin()
				.where(user.id.eq(id))
				.fetchOne()
		);
	}
}

