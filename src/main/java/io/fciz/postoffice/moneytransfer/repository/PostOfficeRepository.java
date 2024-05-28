package io.fciz.postoffice.moneytransfer.repository;

import io.fciz.postoffice.moneytransfer.domain.PostOffice;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PostOffice entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PostOfficeRepository extends JpaRepository<PostOffice, Long>, JpaSpecificationExecutor<PostOffice> {}
