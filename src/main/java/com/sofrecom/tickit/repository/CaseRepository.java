package com.sofrecom.tickit.repository;

import com.sofrecom.tickit.model.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

/**
 * Repository for the Case entity.
 */
@Repository
public interface CaseRepository extends JpaRepository<Case, UUID> {
    // Additional query methods can be declared here if needed.
}
