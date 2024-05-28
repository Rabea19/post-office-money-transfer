package io.fciz.postoffice.moneytransfer.service;

import io.fciz.postoffice.moneytransfer.domain.*; // for static metamodels
import io.fciz.postoffice.moneytransfer.domain.Citizen;
import io.fciz.postoffice.moneytransfer.repository.CitizenRepository;
import io.fciz.postoffice.moneytransfer.repository.search.CitizenSearchRepository;
import io.fciz.postoffice.moneytransfer.service.criteria.CitizenCriteria;
import io.fciz.postoffice.moneytransfer.service.dto.CitizenDTO;
import io.fciz.postoffice.moneytransfer.service.mapper.CitizenMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Citizen} entities in the database.
 * The main input is a {@link CitizenCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CitizenDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CitizenQueryService extends QueryService<Citizen> {

    private final Logger log = LoggerFactory.getLogger(CitizenQueryService.class);

    private final CitizenRepository citizenRepository;

    private final CitizenMapper citizenMapper;

    private final CitizenSearchRepository citizenSearchRepository;

    public CitizenQueryService(
        CitizenRepository citizenRepository,
        CitizenMapper citizenMapper,
        CitizenSearchRepository citizenSearchRepository
    ) {
        this.citizenRepository = citizenRepository;
        this.citizenMapper = citizenMapper;
        this.citizenSearchRepository = citizenSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link CitizenDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CitizenDTO> findByCriteria(CitizenCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Citizen> specification = createSpecification(criteria);
        return citizenRepository.findAll(specification, page).map(citizenMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CitizenCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Citizen> specification = createSpecification(criteria);
        return citizenRepository.count(specification);
    }

    /**
     * Function to convert {@link CitizenCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Citizen> createSpecification(CitizenCriteria criteria) {
        Specification<Citizen> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Citizen_.id));
            }
            if (criteria.getNationalId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNationalId(), Citizen_.nationalId));
            }
            if (criteria.getFirstName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFirstName(), Citizen_.firstName));
            }
            if (criteria.getLastName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastName(), Citizen_.lastName));
            }
            if (criteria.getPhoneNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhoneNumber(), Citizen_.phoneNumber));
            }
        }
        return specification;
    }
}
