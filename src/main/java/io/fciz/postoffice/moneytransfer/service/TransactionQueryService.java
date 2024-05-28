package io.fciz.postoffice.moneytransfer.service;

import io.fciz.postoffice.moneytransfer.domain.*; // for static metamodels
import io.fciz.postoffice.moneytransfer.domain.Transaction;
import io.fciz.postoffice.moneytransfer.repository.TransactionRepository;
import io.fciz.postoffice.moneytransfer.repository.search.TransactionSearchRepository;
import io.fciz.postoffice.moneytransfer.service.criteria.TransactionCriteria;
import io.fciz.postoffice.moneytransfer.service.dto.TransactionDTO;
import io.fciz.postoffice.moneytransfer.service.mapper.TransactionMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Transaction} entities in the database.
 * The main input is a {@link TransactionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TransactionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TransactionQueryService extends QueryService<Transaction> {

    private final Logger log = LoggerFactory.getLogger(TransactionQueryService.class);

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    private final TransactionSearchRepository transactionSearchRepository;

    public TransactionQueryService(
        TransactionRepository transactionRepository,
        TransactionMapper transactionMapper,
        TransactionSearchRepository transactionSearchRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.transactionSearchRepository = transactionSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link TransactionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TransactionDTO> findByCriteria(TransactionCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Transaction> specification = createSpecification(criteria);
        return transactionRepository.findAll(specification, page).map(transactionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TransactionCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Transaction> specification = createSpecification(criteria);
        return transactionRepository.count(specification);
    }

    /**
     * Function to convert {@link TransactionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Transaction> createSpecification(TransactionCriteria criteria) {
        Specification<Transaction> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Transaction_.id));
            }
            if (criteria.getAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAmount(), Transaction_.amount));
            }
            if (criteria.getTransactionDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTransactionDate(), Transaction_.transactionDate));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Transaction_.status));
            }
            if (criteria.getSenderId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getSenderId(), root -> root.join(Transaction_.sender, JoinType.LEFT).get(Citizen_.id))
                );
            }
            if (criteria.getReceiverId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getReceiverId(), root -> root.join(Transaction_.receiver, JoinType.LEFT).get(Citizen_.id))
                );
            }
            if (criteria.getSenderPostOfficeId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getSenderPostOfficeId(),
                        root -> root.join(Transaction_.senderPostOffice, JoinType.LEFT).get(PostOffice_.id)
                    )
                );
            }
            if (criteria.getReceiverPostOfficeId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getReceiverPostOfficeId(),
                        root -> root.join(Transaction_.receiverPostOffice, JoinType.LEFT).get(PostOffice_.id)
                    )
                );
            }
        }
        return specification;
    }
}
