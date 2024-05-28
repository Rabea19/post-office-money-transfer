package io.fciz.postoffice.moneytransfer.service;

import io.fciz.postoffice.moneytransfer.domain.*; // for static metamodels
import io.fciz.postoffice.moneytransfer.domain.PostOffice;
import io.fciz.postoffice.moneytransfer.repository.PostOfficeRepository;
import io.fciz.postoffice.moneytransfer.repository.search.PostOfficeSearchRepository;
import io.fciz.postoffice.moneytransfer.service.criteria.PostOfficeCriteria;
import io.fciz.postoffice.moneytransfer.service.dto.PostOfficeDTO;
import io.fciz.postoffice.moneytransfer.service.mapper.PostOfficeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link PostOffice} entities in the database.
 * The main input is a {@link PostOfficeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link PostOfficeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PostOfficeQueryService extends QueryService<PostOffice> {

    private final Logger log = LoggerFactory.getLogger(PostOfficeQueryService.class);

    private final PostOfficeRepository postOfficeRepository;

    private final PostOfficeMapper postOfficeMapper;

    private final PostOfficeSearchRepository postOfficeSearchRepository;

    public PostOfficeQueryService(
        PostOfficeRepository postOfficeRepository,
        PostOfficeMapper postOfficeMapper,
        PostOfficeSearchRepository postOfficeSearchRepository
    ) {
        this.postOfficeRepository = postOfficeRepository;
        this.postOfficeMapper = postOfficeMapper;
        this.postOfficeSearchRepository = postOfficeSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link PostOfficeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PostOfficeDTO> findByCriteria(PostOfficeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PostOffice> specification = createSpecification(criteria);
        return postOfficeRepository.findAll(specification, page).map(postOfficeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PostOfficeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<PostOffice> specification = createSpecification(criteria);
        return postOfficeRepository.count(specification);
    }

    /**
     * Function to convert {@link PostOfficeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PostOffice> createSpecification(PostOfficeCriteria criteria) {
        Specification<PostOffice> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), PostOffice_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), PostOffice_.name));
            }
            if (criteria.getStreetAddress() != null) {
                specification = specification.and(buildStringSpecification(criteria.getStreetAddress(), PostOffice_.streetAddress));
            }
            if (criteria.getCity() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCity(), PostOffice_.city));
            }
            if (criteria.getState() != null) {
                specification = specification.and(buildStringSpecification(criteria.getState(), PostOffice_.state));
            }
            if (criteria.getPostalCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPostalCode(), PostOffice_.postalCode));
            }
        }
        return specification;
    }
}
