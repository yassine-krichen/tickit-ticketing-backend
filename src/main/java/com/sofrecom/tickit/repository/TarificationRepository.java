package com.sofrecom.tickit.repository;

import com.sofrecom.tickit.model.Tarification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Tarification} entities.
 */
@Repository
public interface TarificationRepository extends JpaRepository<Tarification, String> {
}