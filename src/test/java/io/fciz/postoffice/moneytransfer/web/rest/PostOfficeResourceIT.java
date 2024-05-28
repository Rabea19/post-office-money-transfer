package io.fciz.postoffice.moneytransfer.web.rest;

import static io.fciz.postoffice.moneytransfer.domain.PostOfficeAsserts.*;
import static io.fciz.postoffice.moneytransfer.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fciz.postoffice.moneytransfer.IntegrationTest;
import io.fciz.postoffice.moneytransfer.domain.PostOffice;
import io.fciz.postoffice.moneytransfer.repository.PostOfficeRepository;
import io.fciz.postoffice.moneytransfer.repository.search.PostOfficeSearchRepository;
import io.fciz.postoffice.moneytransfer.service.dto.PostOfficeDTO;
import io.fciz.postoffice.moneytransfer.service.mapper.PostOfficeMapper;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PostOfficeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PostOfficeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_STREET_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_STREET_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_STATE = "AAAAAAAAAA";
    private static final String UPDATED_STATE = "BBBBBBBBBB";

    private static final String DEFAULT_POSTAL_CODE = "24578-5905";
    private static final String UPDATED_POSTAL_CODE = "48121";

    private static final String ENTITY_API_URL = "/api/post-offices";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/post-offices/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PostOfficeRepository postOfficeRepository;

    @Autowired
    private PostOfficeMapper postOfficeMapper;

    @Autowired
    private PostOfficeSearchRepository postOfficeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPostOfficeMockMvc;

    private PostOffice postOffice;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PostOffice createEntity(EntityManager em) {
        PostOffice postOffice = new PostOffice()
            .name(DEFAULT_NAME)
            .streetAddress(DEFAULT_STREET_ADDRESS)
            .city(DEFAULT_CITY)
            .state(DEFAULT_STATE)
            .postalCode(DEFAULT_POSTAL_CODE);
        return postOffice;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PostOffice createUpdatedEntity(EntityManager em) {
        PostOffice postOffice = new PostOffice()
            .name(UPDATED_NAME)
            .streetAddress(UPDATED_STREET_ADDRESS)
            .city(UPDATED_CITY)
            .state(UPDATED_STATE)
            .postalCode(UPDATED_POSTAL_CODE);
        return postOffice;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        postOfficeSearchRepository.deleteAll();
        assertThat(postOfficeSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        postOffice = createEntity(em);
    }

    @Test
    @Transactional
    void createPostOffice() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
        // Create the PostOffice
        PostOfficeDTO postOfficeDTO = postOfficeMapper.toDto(postOffice);
        var returnedPostOfficeDTO = om.readValue(
            restPostOfficeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postOfficeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PostOfficeDTO.class
        );

        // Validate the PostOffice in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPostOffice = postOfficeMapper.toEntity(returnedPostOfficeDTO);
        assertPostOfficeUpdatableFieldsEquals(returnedPostOffice, getPersistedPostOffice(returnedPostOffice));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
    }

    @Test
    @Transactional
    void createPostOfficeWithExistingId() throws Exception {
        // Create the PostOffice with an existing ID
        postOffice.setId(1L);
        PostOfficeDTO postOfficeDTO = postOfficeMapper.toDto(postOffice);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restPostOfficeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postOfficeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PostOffice in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
        // set the field null
        postOffice.setName(null);

        // Create the PostOffice, which fails.
        PostOfficeDTO postOfficeDTO = postOfficeMapper.toDto(postOffice);

        restPostOfficeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postOfficeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStreetAddressIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
        // set the field null
        postOffice.setStreetAddress(null);

        // Create the PostOffice, which fails.
        PostOfficeDTO postOfficeDTO = postOfficeMapper.toDto(postOffice);

        restPostOfficeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postOfficeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
        // set the field null
        postOffice.setCity(null);

        // Create the PostOffice, which fails.
        PostOfficeDTO postOfficeDTO = postOfficeMapper.toDto(postOffice);

        restPostOfficeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postOfficeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
        // set the field null
        postOffice.setState(null);

        // Create the PostOffice, which fails.
        PostOfficeDTO postOfficeDTO = postOfficeMapper.toDto(postOffice);

        restPostOfficeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postOfficeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPostalCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
        // set the field null
        postOffice.setPostalCode(null);

        // Create the PostOffice, which fails.
        PostOfficeDTO postOfficeDTO = postOfficeMapper.toDto(postOffice);

        restPostOfficeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postOfficeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllPostOffices() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        // Get all the postOfficeList
        restPostOfficeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(postOffice.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].streetAddress").value(hasItem(DEFAULT_STREET_ADDRESS)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE)))
            .andExpect(jsonPath("$.[*].postalCode").value(hasItem(DEFAULT_POSTAL_CODE)));
    }

    @Test
    @Transactional
    void getPostOffice() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        // Get the postOffice
        restPostOfficeMockMvc
            .perform(get(ENTITY_API_URL_ID, postOffice.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(postOffice.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.streetAddress").value(DEFAULT_STREET_ADDRESS))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE))
            .andExpect(jsonPath("$.postalCode").value(DEFAULT_POSTAL_CODE));
    }

    @Test
    @Transactional
    void getPostOfficesByIdFiltering() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        Long id = postOffice.getId();

        defaultPostOfficeFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPostOfficeFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPostOfficeFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPostOfficesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        // Get all the postOfficeList where name equals to
        defaultPostOfficeFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPostOfficesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        // Get all the postOfficeList where name in
        defaultPostOfficeFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPostOfficesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        // Get all the postOfficeList where name is not null
        defaultPostOfficeFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllPostOfficesByNameContainsSomething() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        // Get all the postOfficeList where name contains
        defaultPostOfficeFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPostOfficesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        // Get all the postOfficeList where name does not contain
        defaultPostOfficeFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllPostOfficesByStreetAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        // Get all the postOfficeList where streetAddress equals to
        defaultPostOfficeFiltering("streetAddress.equals=" + DEFAULT_STREET_ADDRESS, "streetAddress.equals=" + UPDATED_STREET_ADDRESS);
    }

    @Test
    @Transactional
    void getAllPostOfficesByStreetAddressIsInShouldWork() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        // Get all the postOfficeList where streetAddress in
        defaultPostOfficeFiltering(
            "streetAddress.in=" + DEFAULT_STREET_ADDRESS + "," + UPDATED_STREET_ADDRESS,
            "streetAddress.in=" + UPDATED_STREET_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllPostOfficesByStreetAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        // Get all the postOfficeList where streetAddress is not null
        defaultPostOfficeFiltering("streetAddress.specified=true", "streetAddress.specified=false");
    }

    @Test
    @Transactional
    void getAllPostOfficesByStreetAddressContainsSomething() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        // Get all the postOfficeList where streetAddress contains
        defaultPostOfficeFiltering("streetAddress.contains=" + DEFAULT_STREET_ADDRESS, "streetAddress.contains=" + UPDATED_STREET_ADDRESS);
    }

    @Test
    @Transactional
    void getAllPostOfficesByStreetAddressNotContainsSomething() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        // Get all the postOfficeList where streetAddress does not contain
        defaultPostOfficeFiltering(
            "streetAddress.doesNotContain=" + UPDATED_STREET_ADDRESS,
            "streetAddress.doesNotContain=" + DEFAULT_STREET_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllPostOfficesByCityIsEqualToSomething() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        // Get all the postOfficeList where city equals to
        defaultPostOfficeFiltering("city.equals=" + DEFAULT_CITY, "city.equals=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllPostOfficesByCityIsInShouldWork() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        // Get all the postOfficeList where city in
        defaultPostOfficeFiltering("city.in=" + DEFAULT_CITY + "," + UPDATED_CITY, "city.in=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllPostOfficesByCityIsNullOrNotNull() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        // Get all the postOfficeList where city is not null
        defaultPostOfficeFiltering("city.specified=true", "city.specified=false");
    }

    @Test
    @Transactional
    void getAllPostOfficesByCityContainsSomething() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        // Get all the postOfficeList where city contains
        defaultPostOfficeFiltering("city.contains=" + DEFAULT_CITY, "city.contains=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllPostOfficesByCityNotContainsSomething() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        // Get all the postOfficeList where city does not contain
        defaultPostOfficeFiltering("city.doesNotContain=" + UPDATED_CITY, "city.doesNotContain=" + DEFAULT_CITY);
    }

    @Test
    @Transactional
    void getAllPostOfficesByStateIsEqualToSomething() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        // Get all the postOfficeList where state equals to
        defaultPostOfficeFiltering("state.equals=" + DEFAULT_STATE, "state.equals=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllPostOfficesByStateIsInShouldWork() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        // Get all the postOfficeList where state in
        defaultPostOfficeFiltering("state.in=" + DEFAULT_STATE + "," + UPDATED_STATE, "state.in=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllPostOfficesByStateIsNullOrNotNull() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        // Get all the postOfficeList where state is not null
        defaultPostOfficeFiltering("state.specified=true", "state.specified=false");
    }

    @Test
    @Transactional
    void getAllPostOfficesByStateContainsSomething() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        // Get all the postOfficeList where state contains
        defaultPostOfficeFiltering("state.contains=" + DEFAULT_STATE, "state.contains=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllPostOfficesByStateNotContainsSomething() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        // Get all the postOfficeList where state does not contain
        defaultPostOfficeFiltering("state.doesNotContain=" + UPDATED_STATE, "state.doesNotContain=" + DEFAULT_STATE);
    }

    @Test
    @Transactional
    void getAllPostOfficesByPostalCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        // Get all the postOfficeList where postalCode equals to
        defaultPostOfficeFiltering("postalCode.equals=" + DEFAULT_POSTAL_CODE, "postalCode.equals=" + UPDATED_POSTAL_CODE);
    }

    @Test
    @Transactional
    void getAllPostOfficesByPostalCodeIsInShouldWork() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        // Get all the postOfficeList where postalCode in
        defaultPostOfficeFiltering(
            "postalCode.in=" + DEFAULT_POSTAL_CODE + "," + UPDATED_POSTAL_CODE,
            "postalCode.in=" + UPDATED_POSTAL_CODE
        );
    }

    @Test
    @Transactional
    void getAllPostOfficesByPostalCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        // Get all the postOfficeList where postalCode is not null
        defaultPostOfficeFiltering("postalCode.specified=true", "postalCode.specified=false");
    }

    @Test
    @Transactional
    void getAllPostOfficesByPostalCodeContainsSomething() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        // Get all the postOfficeList where postalCode contains
        defaultPostOfficeFiltering("postalCode.contains=" + DEFAULT_POSTAL_CODE, "postalCode.contains=" + UPDATED_POSTAL_CODE);
    }

    @Test
    @Transactional
    void getAllPostOfficesByPostalCodeNotContainsSomething() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        // Get all the postOfficeList where postalCode does not contain
        defaultPostOfficeFiltering("postalCode.doesNotContain=" + UPDATED_POSTAL_CODE, "postalCode.doesNotContain=" + DEFAULT_POSTAL_CODE);
    }

    private void defaultPostOfficeFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPostOfficeShouldBeFound(shouldBeFound);
        defaultPostOfficeShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPostOfficeShouldBeFound(String filter) throws Exception {
        restPostOfficeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(postOffice.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].streetAddress").value(hasItem(DEFAULT_STREET_ADDRESS)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE)))
            .andExpect(jsonPath("$.[*].postalCode").value(hasItem(DEFAULT_POSTAL_CODE)));

        // Check, that the count call also returns 1
        restPostOfficeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPostOfficeShouldNotBeFound(String filter) throws Exception {
        restPostOfficeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPostOfficeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPostOffice() throws Exception {
        // Get the postOffice
        restPostOfficeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPostOffice() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        postOfficeSearchRepository.save(postOffice);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());

        // Update the postOffice
        PostOffice updatedPostOffice = postOfficeRepository.findById(postOffice.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPostOffice are not directly saved in db
        em.detach(updatedPostOffice);
        updatedPostOffice
            .name(UPDATED_NAME)
            .streetAddress(UPDATED_STREET_ADDRESS)
            .city(UPDATED_CITY)
            .state(UPDATED_STATE)
            .postalCode(UPDATED_POSTAL_CODE);
        PostOfficeDTO postOfficeDTO = postOfficeMapper.toDto(updatedPostOffice);

        restPostOfficeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, postOfficeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(postOfficeDTO))
            )
            .andExpect(status().isOk());

        // Validate the PostOffice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPostOfficeToMatchAllProperties(updatedPostOffice);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<PostOffice> postOfficeSearchList = Streamable.of(postOfficeSearchRepository.findAll()).toList();
                PostOffice testPostOfficeSearch = postOfficeSearchList.get(searchDatabaseSizeAfter - 1);

                assertPostOfficeAllPropertiesEquals(testPostOfficeSearch, updatedPostOffice);
            });
    }

    @Test
    @Transactional
    void putNonExistingPostOffice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
        postOffice.setId(longCount.incrementAndGet());

        // Create the PostOffice
        PostOfficeDTO postOfficeDTO = postOfficeMapper.toDto(postOffice);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPostOfficeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, postOfficeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(postOfficeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PostOffice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchPostOffice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
        postOffice.setId(longCount.incrementAndGet());

        // Create the PostOffice
        PostOfficeDTO postOfficeDTO = postOfficeMapper.toDto(postOffice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostOfficeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(postOfficeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PostOffice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPostOffice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
        postOffice.setId(longCount.incrementAndGet());

        // Create the PostOffice
        PostOfficeDTO postOfficeDTO = postOfficeMapper.toDto(postOffice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostOfficeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postOfficeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PostOffice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdatePostOfficeWithPatch() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the postOffice using partial update
        PostOffice partialUpdatedPostOffice = new PostOffice();
        partialUpdatedPostOffice.setId(postOffice.getId());

        partialUpdatedPostOffice.name(UPDATED_NAME).city(UPDATED_CITY).state(UPDATED_STATE);

        restPostOfficeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPostOffice.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPostOffice))
            )
            .andExpect(status().isOk());

        // Validate the PostOffice in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPostOfficeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPostOffice, postOffice),
            getPersistedPostOffice(postOffice)
        );
    }

    @Test
    @Transactional
    void fullUpdatePostOfficeWithPatch() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the postOffice using partial update
        PostOffice partialUpdatedPostOffice = new PostOffice();
        partialUpdatedPostOffice.setId(postOffice.getId());

        partialUpdatedPostOffice
            .name(UPDATED_NAME)
            .streetAddress(UPDATED_STREET_ADDRESS)
            .city(UPDATED_CITY)
            .state(UPDATED_STATE)
            .postalCode(UPDATED_POSTAL_CODE);

        restPostOfficeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPostOffice.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPostOffice))
            )
            .andExpect(status().isOk());

        // Validate the PostOffice in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPostOfficeUpdatableFieldsEquals(partialUpdatedPostOffice, getPersistedPostOffice(partialUpdatedPostOffice));
    }

    @Test
    @Transactional
    void patchNonExistingPostOffice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
        postOffice.setId(longCount.incrementAndGet());

        // Create the PostOffice
        PostOfficeDTO postOfficeDTO = postOfficeMapper.toDto(postOffice);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPostOfficeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, postOfficeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(postOfficeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PostOffice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPostOffice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
        postOffice.setId(longCount.incrementAndGet());

        // Create the PostOffice
        PostOfficeDTO postOfficeDTO = postOfficeMapper.toDto(postOffice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostOfficeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(postOfficeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PostOffice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPostOffice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
        postOffice.setId(longCount.incrementAndGet());

        // Create the PostOffice
        PostOfficeDTO postOfficeDTO = postOfficeMapper.toDto(postOffice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostOfficeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(postOfficeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PostOffice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deletePostOffice() throws Exception {
        // Initialize the database
        postOfficeRepository.saveAndFlush(postOffice);
        postOfficeRepository.save(postOffice);
        postOfficeSearchRepository.save(postOffice);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the postOffice
        restPostOfficeMockMvc
            .perform(delete(ENTITY_API_URL_ID, postOffice.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postOfficeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchPostOffice() throws Exception {
        // Initialize the database
        postOffice = postOfficeRepository.saveAndFlush(postOffice);
        postOfficeSearchRepository.save(postOffice);

        // Search the postOffice
        restPostOfficeMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + postOffice.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(postOffice.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].streetAddress").value(hasItem(DEFAULT_STREET_ADDRESS)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE)))
            .andExpect(jsonPath("$.[*].postalCode").value(hasItem(DEFAULT_POSTAL_CODE)));
    }

    protected long getRepositoryCount() {
        return postOfficeRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected PostOffice getPersistedPostOffice(PostOffice postOffice) {
        return postOfficeRepository.findById(postOffice.getId()).orElseThrow();
    }

    protected void assertPersistedPostOfficeToMatchAllProperties(PostOffice expectedPostOffice) {
        assertPostOfficeAllPropertiesEquals(expectedPostOffice, getPersistedPostOffice(expectedPostOffice));
    }

    protected void assertPersistedPostOfficeToMatchUpdatableProperties(PostOffice expectedPostOffice) {
        assertPostOfficeAllUpdatablePropertiesEquals(expectedPostOffice, getPersistedPostOffice(expectedPostOffice));
    }
}
