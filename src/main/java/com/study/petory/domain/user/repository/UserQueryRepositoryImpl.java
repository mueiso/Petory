package com.study.petory.domain.user.repository;

import static com.study.petory.domain.user.entity.QUser.*;
import static com.study.petory.domain.user.entity.QUserRole.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserStatus;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {

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

	@Override
	public Optional<User> findByIdWithUserRoleAndUserStatus(Long id, UserStatus status) {
		return Optional.ofNullable(
			queryFactory
				.selectFrom(user)
				.leftJoin(user.userRole, userRole).fetchJoin()
				.where(
					user.id.eq(id),
					user.userStatus.eq(status)
				)
				.fetchOne()
		);
	}
}

