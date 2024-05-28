package io.fciz.postoffice.moneytransfer.service;

import io.fciz.postoffice.moneytransfer.domain.Citizen;
import io.fciz.postoffice.moneytransfer.repository.CitizenRepository;
import io.fciz.postoffice.moneytransfer.repository.search.CitizenSearchRepository;
import io.fciz.postoffice.moneytransfer.service.dto.CitizenDTO;
import io.fciz.postoffice.moneytransfer.service.mapper.CitizenMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.fciz.postoffice.moneytransfer.domain.Citizen}.
 */
@Service
@Transactional
public class CitizenService {

    private final Logger log = LoggerFactory.getLogger(CitizenService.class);

    private final CitizenRepository citizenRepository;

    private final CitizenMapper citizenMapper;

    private final CitizenSearchRepository citizenSearchRepository;

    public CitizenService(
        CitizenRepository citizenRepository,
        CitizenMapper citizenMapper,
        CitizenSearchRepository citizenSearchRepository
    ) {
        this.citizenRepository = citizenRepository;
        this.citizenMapper = citizenMapper;
        this.citizenSearchRepository = citizenSearchRepository;
    }

    /**
     * Save a citizen.
     *
     * @param citizenDTO the entity to save.
     * @return the persisted entity.
     */
    public CitizenDTO save(CitizenDTO citizenDTO) {
        log.debug("Request to save Citizen : {}", citizenDTO);
        Citizen citizen = citizenMapper.toEntity(citizenDTO);
        citizen = citizenRepository.save(citizen);
        citizenSearchRepository.index(citizen);
        return citizenMapper.toDto(citizen);
    }

    /**
     * Update a citizen.
     *
     * @param citizenDTO the entity to save.
     * @return the persisted entity.
     */
    public CitizenDTO update(CitizenDTO citizenDTO) {
        log.debug("Request to update Citizen : {}", citizenDTO);
        Citizen citizen = citizenMapper.toEntity(citizenDTO);
        citizen = citizenRepository.save(citizen);
        citizenSearchRepository.index(citizen);
        return citizenMapper.toDto(citizen);
    }

    /**
     * Partially update a citizen.
     *
     * @param citizenDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CitizenDTO> partialUpdate(CitizenDTO citizenDTO) {
        log.debug("Request to partially update Citizen : {}", citizenDTO);

        return citizenRepository
            .findById(citizenDTO.getId())
            .map(existingCitizen -> {
                citizenMapper.partialUpdate(existingCitizen, citizenDTO);

                return existingCitizen;
            })
            .map(citizenRepository::save)
            .map(savedCitizen -> {
                citizenSearchRepository.index(savedCitizen);
                return savedCitizen;
            })
            .map(citizenMapper::toDto);
    }

    /**
     * Get one citizen by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CitizenDTO> findOne(Long id) {
        log.debug("Request to get Citizen : {}", id);
        return citizenRepository.findById(id).map(citizenMapper::toDto);
    }

    /**
     * Delete the citizen by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Citizen : {}", id);
        citizenRepository.deleteById(id);
        citizenSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the citizen corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CitizenDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Citizens for query {}", query);
        return citizenSearchRepository.search(query, pageable).map(citizenMapper::toDto);
    }
}
