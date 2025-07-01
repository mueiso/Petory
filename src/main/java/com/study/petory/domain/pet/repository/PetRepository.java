package com.study.petory.domain.pet.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.pet.entity.Pet;
import com.study.petory.domain.user.entity.User;

public interface PetRepository extends JpaRepository<Pet, Long> {

	Optional<Pet> findPetById(Long id);

	Page<Pet> findAllByUser(User user, Pageable pageable);
}
