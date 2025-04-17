package com.sofrecom.tickit.repository;

import com.sofrecom.tickit.model.Prestation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Prestation} entities.
 */
@Repository
public interface PrestationRepository extends JpaRepository<Prestation, Integer> {
}