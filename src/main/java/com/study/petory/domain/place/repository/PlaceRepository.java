package com.study.petory.domain.place.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.user.entity.User;

public interface PlaceRepository extends JpaRepository<Place, Long>, PlaceCustomRepository {
	Optional<Place> findByPlaceNameAndAddress(String placeName, String address);

	List<Place> findByUser(User user);
}
