package io.fciz.postoffice.moneytransfer.web.rest;

import io.fciz.postoffice.moneytransfer.repository.PostOfficeRepository;
import io.fciz.postoffice.moneytransfer.service.PostOfficeQueryService;
import io.fciz.postoffice.moneytransfer.service.PostOfficeService;
import io.fciz.postoffice.moneytransfer.service.criteria.PostOfficeCriteria;
import io.fciz.postoffice.moneytransfer.service.dto.PostOfficeDTO;
import io.fciz.postoffice.moneytransfer.web.rest.errors.BadRequestAlertException;
import io.fciz.postoffice.moneytransfer.web.rest.errors.ElasticsearchExceptionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link io.fciz.postoffice.moneytransfer.domain.PostOffice}.
 */
@RestController
@RequestMapping("/api/post-offices")
public class PostOfficeResource {

    private final Logger log = LoggerFactory.getLogger(PostOfficeResource.class);

    private static final String ENTITY_NAME = "postOffice";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PostOfficeService postOfficeService;

    private final PostOfficeRepository postOfficeRepository;

    private final PostOfficeQueryService postOfficeQueryService;

    public PostOfficeResource(
        PostOfficeService postOfficeService,
        PostOfficeRepository postOfficeRepository,
        PostOfficeQueryService postOfficeQueryService
    ) {
        this.postOfficeService = postOfficeService;
        this.postOfficeRepository = postOfficeRepository;
        this.postOfficeQueryService = postOfficeQueryService;
    }

    /**
     * {@code POST  /post-offices} : Create a new postOffice.
     *
     * @param postOfficeDTO the postOfficeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new postOfficeDTO, or with status {@code 400 (Bad Request)} if the postOffice has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PostOfficeDTO> createPostOffice(@Valid @RequestBody PostOfficeDTO postOfficeDTO) throws URISyntaxException {
        log.debug("REST request to save PostOffice : {}", postOfficeDTO);
        if (postOfficeDTO.getId() != null) {
            throw new BadRequestAlertException("A new postOffice cannot already have an ID", ENTITY_NAME, "idexists");
        }
        postOfficeDTO = postOfficeService.save(postOfficeDTO);
        return ResponseEntity.created(new URI("/api/post-offices/" + postOfficeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, postOfficeDTO.getId().toString()))
            .body(postOfficeDTO);
    }

    /**
     * {@code PUT  /post-offices/:id} : Updates an existing postOffice.
     *
     * @param id the id of the postOfficeDTO to save.
     * @param postOfficeDTO the postOfficeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated postOfficeDTO,
     * or with status {@code 400 (Bad Request)} if the postOfficeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the postOfficeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PostOfficeDTO> updatePostOffice(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PostOfficeDTO postOfficeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update PostOffice : {}, {}", id, postOfficeDTO);
        if (postOfficeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, postOfficeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!postOfficeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        postOfficeDTO = postOfficeService.update(postOfficeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, postOfficeDTO.getId().toString()))
            .body(postOfficeDTO);
    }

    /**
     * {@code PATCH  /post-offices/:id} : Partial updates given fields of an existing postOffice, field will ignore if it is null
     *
     * @param id the id of the postOfficeDTO to save.
     * @param postOfficeDTO the postOfficeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated postOfficeDTO,
     * or with status {@code 400 (Bad Request)} if the postOfficeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the postOfficeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the postOfficeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PostOfficeDTO> partialUpdatePostOffice(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PostOfficeDTO postOfficeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update PostOffice partially : {}, {}", id, postOfficeDTO);
        if (postOfficeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, postOfficeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!postOfficeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PostOfficeDTO> result = postOfficeService.partialUpdate(postOfficeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, postOfficeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /post-offices} : get all the postOffices.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of postOffices in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PostOfficeDTO>> getAllPostOffices(
        PostOfficeCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get PostOffices by criteria: {}", criteria);

        Page<PostOfficeDTO> page = postOfficeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /post-offices/count} : count all the postOffices.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countPostOffices(PostOfficeCriteria criteria) {
        log.debug("REST request to count PostOffices by criteria: {}", criteria);
        return ResponseEntity.ok().body(postOfficeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /post-offices/:id} : get the "id" postOffice.
     *
     * @param id the id of the postOfficeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the postOfficeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostOfficeDTO> getPostOffice(@PathVariable("id") Long id) {
        log.debug("REST request to get PostOffice : {}", id);
        Optional<PostOfficeDTO> postOfficeDTO = postOfficeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(postOfficeDTO);
    }

    /**
     * {@code DELETE  /post-offices/:id} : delete the "id" postOffice.
     *
     * @param id the id of the postOfficeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePostOffice(@PathVariable("id") Long id) {
        log.debug("REST request to delete PostOffice : {}", id);
        postOfficeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /post-offices/_search?query=:query} : search for the postOffice corresponding
     * to the query.
     *
     * @param query the query of the postOffice search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<PostOfficeDTO>> searchPostOffices(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of PostOffices for query {}", query);
        try {
            Page<PostOfficeDTO> page = postOfficeService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
