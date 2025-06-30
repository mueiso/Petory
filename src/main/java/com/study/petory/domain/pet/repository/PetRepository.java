package com.study.petory.domain.pet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.pet.entity.Pet;

public interface PetRepository extends JpaRepository<Pet, Long> {

	Optional<Pet> findPetById(Long id);
}
