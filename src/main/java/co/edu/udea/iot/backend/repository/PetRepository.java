package co.edu.udea.iot.backend.repository;

import co.edu.udea.iot.backend.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Integer> {

    Optional<Pet> findByName(String name);
}
