package io.fciz.postoffice.moneytransfer.repository;

import io.fciz.postoffice.moneytransfer.domain.Citizen;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Citizen entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CitizenRepository extends JpaRepository<Citizen, Long>, JpaSpecificationExecutor<Citizen> {}
