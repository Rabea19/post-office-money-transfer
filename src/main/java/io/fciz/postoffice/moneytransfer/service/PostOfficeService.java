package io.fciz.postoffice.moneytransfer.service;

import io.fciz.postoffice.moneytransfer.domain.PostOffice;
import io.fciz.postoffice.moneytransfer.repository.PostOfficeRepository;
import io.fciz.postoffice.moneytransfer.repository.search.PostOfficeSearchRepository;
import io.fciz.postoffice.moneytransfer.service.dto.PostOfficeDTO;
import io.fciz.postoffice.moneytransfer.service.mapper.PostOfficeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.fciz.postoffice.moneytransfer.domain.PostOffice}.
 */
@Service
@Transactional
public class PostOfficeService {

    private final Logger log = LoggerFactory.getLogger(PostOfficeService.class);

    private final PostOfficeRepository postOfficeRepository;

    private final PostOfficeMapper postOfficeMapper;

    private final PostOfficeSearchRepository postOfficeSearchRepository;

    public PostOfficeService(
        PostOfficeRepository postOfficeRepository,
        PostOfficeMapper postOfficeMapper,
        PostOfficeSearchRepository postOfficeSearchRepository
    ) {
        this.postOfficeRepository = postOfficeRepository;
        this.postOfficeMapper = postOfficeMapper;
        this.postOfficeSearchRepository = postOfficeSearchRepository;
    }

    /**
     * Save a postOffice.
     *
     * @param postOfficeDTO the entity to save.
     * @return the persisted entity.
     */
    public PostOfficeDTO save(PostOfficeDTO postOfficeDTO) {
        log.debug("Request to save PostOffice : {}", postOfficeDTO);
        PostOffice postOffice = postOfficeMapper.toEntity(postOfficeDTO);
        postOffice = postOfficeRepository.save(postOffice);
        postOfficeSearchRepository.index(postOffice);
        return postOfficeMapper.toDto(postOffice);
    }

    /**
     * Update a postOffice.
     *
     * @param postOfficeDTO the entity to save.
     * @return the persisted entity.
     */
    public PostOfficeDTO update(PostOfficeDTO postOfficeDTO) {
        log.debug("Request to update PostOffice : {}", postOfficeDTO);
        PostOffice postOffice = postOfficeMapper.toEntity(postOfficeDTO);
        postOffice = postOfficeRepository.save(postOffice);
        postOfficeSearchRepository.index(postOffice);
        return postOfficeMapper.toDto(postOffice);
    }

    /**
     * Partially update a postOffice.
     *
     * @param postOfficeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PostOfficeDTO> partialUpdate(PostOfficeDTO postOfficeDTO) {
        log.debug("Request to partially update PostOffice : {}", postOfficeDTO);

        return postOfficeRepository
            .findById(postOfficeDTO.getId())
            .map(existingPostOffice -> {
                postOfficeMapper.partialUpdate(existingPostOffice, postOfficeDTO);

                return existingPostOffice;
            })
            .map(postOfficeRepository::save)
            .map(savedPostOffice -> {
                postOfficeSearchRepository.index(savedPostOffice);
                return savedPostOffice;
            })
            .map(postOfficeMapper::toDto);
    }

    /**
     * Get one postOffice by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PostOfficeDTO> findOne(Long id) {
        log.debug("Request to get PostOffice : {}", id);
        return postOfficeRepository.findById(id).map(postOfficeMapper::toDto);
    }

    /**
     * Delete the postOffice by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete PostOffice : {}", id);
        postOfficeRepository.deleteById(id);
        postOfficeSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the postOffice corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PostOfficeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of PostOffices for query {}", query);
        return postOfficeSearchRepository.search(query, pageable).map(postOfficeMapper::toDto);
    }
}
