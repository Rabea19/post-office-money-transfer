package io.fciz.postoffice.moneytransfer.web.rest;

import static io.fciz.postoffice.moneytransfer.domain.CitizenAsserts.*;
import static io.fciz.postoffice.moneytransfer.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fciz.postoffice.moneytransfer.IntegrationTest;
import io.fciz.postoffice.moneytransfer.domain.Citizen;
import io.fciz.postoffice.moneytransfer.repository.CitizenRepository;
import io.fciz.postoffice.moneytransfer.repository.search.CitizenSearchRepository;
import io.fciz.postoffice.moneytransfer.service.dto.CitizenDTO;
import io.fciz.postoffice.moneytransfer.service.mapper.CitizenMapper;
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
 * Integration tests for the {@link CitizenResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CitizenResourceIT {

    private static final String DEFAULT_NATIONAL_ID = "71940801959153";
    private static final String UPDATED_NATIONAL_ID = "97184495121957";

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "63910685560";
    private static final String UPDATED_PHONE_NUMBER = "58176952289";

    private static final String ENTITY_API_URL = "/api/citizens";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/citizens/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CitizenRepository citizenRepository;

    @Autowired
    private CitizenMapper citizenMapper;

    @Autowired
    private CitizenSearchRepository citizenSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCitizenMockMvc;

    private Citizen citizen;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Citizen createEntity(EntityManager em) {
        Citizen citizen = new Citizen()
            .nationalId(DEFAULT_NATIONAL_ID)
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .phoneNumber(DEFAULT_PHONE_NUMBER);
        return citizen;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Citizen createUpdatedEntity(EntityManager em) {
        Citizen citizen = new Citizen()
            .nationalId(UPDATED_NATIONAL_ID)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .phoneNumber(UPDATED_PHONE_NUMBER);
        return citizen;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        citizenSearchRepository.deleteAll();
        assertThat(citizenSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        citizen = createEntity(em);
    }

    @Test
    @Transactional
    void createCitizen() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citizenSearchRepository.findAll());
        // Create the Citizen
        CitizenDTO citizenDTO = citizenMapper.toDto(citizen);
        var returnedCitizenDTO = om.readValue(
            restCitizenMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(citizenDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CitizenDTO.class
        );

        // Validate the Citizen in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCitizen = citizenMapper.toEntity(returnedCitizenDTO);
        assertCitizenUpdatableFieldsEquals(returnedCitizen, getPersistedCitizen(returnedCitizen));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(citizenSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
    }

    @Test
    @Transactional
    void createCitizenWithExistingId() throws Exception {
        // Create the Citizen with an existing ID
        citizen.setId(1L);
        CitizenDTO citizenDTO = citizenMapper.toDto(citizen);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citizenSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCitizenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(citizenDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Citizen in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citizenSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNationalIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citizenSearchRepository.findAll());
        // set the field null
        citizen.setNationalId(null);

        // Create the Citizen, which fails.
        CitizenDTO citizenDTO = citizenMapper.toDto(citizen);

        restCitizenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(citizenDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citizenSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkFirstNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citizenSearchRepository.findAll());
        // set the field null
        citizen.setFirstName(null);

        // Create the Citizen, which fails.
        CitizenDTO citizenDTO = citizenMapper.toDto(citizen);

        restCitizenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(citizenDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citizenSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkLastNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citizenSearchRepository.findAll());
        // set the field null
        citizen.setLastName(null);

        // Create the Citizen, which fails.
        CitizenDTO citizenDTO = citizenMapper.toDto(citizen);

        restCitizenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(citizenDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citizenSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPhoneNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citizenSearchRepository.findAll());
        // set the field null
        citizen.setPhoneNumber(null);

        // Create the Citizen, which fails.
        CitizenDTO citizenDTO = citizenMapper.toDto(citizen);

        restCitizenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(citizenDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citizenSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCitizens() throws Exception {
        // Initialize the database
        citizenRepository.saveAndFlush(citizen);

        // Get all the citizenList
        restCitizenMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(citizen.getId().intValue())))
            .andExpect(jsonPath("$.[*].nationalId").value(hasItem(DEFAULT_NATIONAL_ID)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)));
    }

    @Test
    @Transactional
    void getCitizen() throws Exception {
        // Initialize the database
        citizenRepository.saveAndFlush(citizen);

        // Get the citizen
        restCitizenMockMvc
            .perform(get(ENTITY_API_URL_ID, citizen.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(citizen.getId().intValue()))
            .andExpect(jsonPath("$.nationalId").value(DEFAULT_NATIONAL_ID))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER));
    }

    @Test
    @Transactional
    void getCitizensByIdFiltering() throws Exception {
        // Initialize the database
        citizenRepository.saveAndFlush(citizen);

        Long id = citizen.getId();

        defaultCitizenFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCitizenFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCitizenFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCitizensByNationalIdIsEqualToSomething() throws Exception {
        // Initialize the database
        citizenRepository.saveAndFlush(citizen);

        // Get all the citizenList where nationalId equals to
        defaultCitizenFiltering("nationalId.equals=" + DEFAULT_NATIONAL_ID, "nationalId.equals=" + UPDATED_NATIONAL_ID);
    }

    @Test
    @Transactional
    void getAllCitizensByNationalIdIsInShouldWork() throws Exception {
        // Initialize the database
        citizenRepository.saveAndFlush(citizen);

        // Get all the citizenList where nationalId in
        defaultCitizenFiltering("nationalId.in=" + DEFAULT_NATIONAL_ID + "," + UPDATED_NATIONAL_ID, "nationalId.in=" + UPDATED_NATIONAL_ID);
    }

    @Test
    @Transactional
    void getAllCitizensByNationalIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        citizenRepository.saveAndFlush(citizen);

        // Get all the citizenList where nationalId is not null
        defaultCitizenFiltering("nationalId.specified=true", "nationalId.specified=false");
    }

    @Test
    @Transactional
    void getAllCitizensByNationalIdContainsSomething() throws Exception {
        // Initialize the database
        citizenRepository.saveAndFlush(citizen);

        // Get all the citizenList where nationalId contains
        defaultCitizenFiltering("nationalId.contains=" + DEFAULT_NATIONAL_ID, "nationalId.contains=" + UPDATED_NATIONAL_ID);
    }

    @Test
    @Transactional
    void getAllCitizensByNationalIdNotContainsSomething() throws Exception {
        // Initialize the database
        citizenRepository.saveAndFlush(citizen);

        // Get all the citizenList where nationalId does not contain
        defaultCitizenFiltering("nationalId.doesNotContain=" + UPDATED_NATIONAL_ID, "nationalId.doesNotContain=" + DEFAULT_NATIONAL_ID);
    }

    @Test
    @Transactional
    void getAllCitizensByFirstNameIsEqualToSomething() throws Exception {
        // Initialize the database
        citizenRepository.saveAndFlush(citizen);

        // Get all the citizenList where firstName equals to
        defaultCitizenFiltering("firstName.equals=" + DEFAULT_FIRST_NAME, "firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllCitizensByFirstNameIsInShouldWork() throws Exception {
        // Initialize the database
        citizenRepository.saveAndFlush(citizen);

        // Get all the citizenList where firstName in
        defaultCitizenFiltering("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME, "firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllCitizensByFirstNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        citizenRepository.saveAndFlush(citizen);

        // Get all the citizenList where firstName is not null
        defaultCitizenFiltering("firstName.specified=true", "firstName.specified=false");
    }

    @Test
    @Transactional
    void getAllCitizensByFirstNameContainsSomething() throws Exception {
        // Initialize the database
        citizenRepository.saveAndFlush(citizen);

        // Get all the citizenList where firstName contains
        defaultCitizenFiltering("firstName.contains=" + DEFAULT_FIRST_NAME, "firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllCitizensByFirstNameNotContainsSomething() throws Exception {
        // Initialize the database
        citizenRepository.saveAndFlush(citizen);

        // Get all the citizenList where firstName does not contain
        defaultCitizenFiltering("firstName.doesNotContain=" + UPDATED_FIRST_NAME, "firstName.doesNotContain=" + DEFAULT_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllCitizensByLastNameIsEqualToSomething() throws Exception {
        // Initialize the database
        citizenRepository.saveAndFlush(citizen);

        // Get all the citizenList where lastName equals to
        defaultCitizenFiltering("lastName.equals=" + DEFAULT_LAST_NAME, "lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllCitizensByLastNameIsInShouldWork() throws Exception {
        // Initialize the database
        citizenRepository.saveAndFlush(citizen);

        // Get all the citizenList where lastName in
        defaultCitizenFiltering("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME, "lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllCitizensByLastNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        citizenRepository.saveAndFlush(citizen);

        // Get all the citizenList where lastName is not null
        defaultCitizenFiltering("lastName.specified=true", "lastName.specified=false");
    }

    @Test
    @Transactional
    void getAllCitizensByLastNameContainsSomething() throws Exception {
        // Initialize the database
        citizenRepository.saveAndFlush(citizen);

        // Get all the citizenList where lastName contains
        defaultCitizenFiltering("lastName.contains=" + DEFAULT_LAST_NAME, "lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllCitizensByLastNameNotContainsSomething() throws Exception {
        // Initialize the database
        citizenRepository.saveAndFlush(citizen);

        // Get all the citizenList where lastName does not contain
        defaultCitizenFiltering("lastName.doesNotContain=" + UPDATED_LAST_NAME, "lastName.doesNotContain=" + DEFAULT_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllCitizensByPhoneNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        citizenRepository.saveAndFlush(citizen);

        // Get all the citizenList where phoneNumber equals to
        defaultCitizenFiltering("phoneNumber.equals=" + DEFAULT_PHONE_NUMBER, "phoneNumber.equals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllCitizensByPhoneNumberIsInShouldWork() throws Exception {
        // Initialize the database
        citizenRepository.saveAndFlush(citizen);

        // Get all the citizenList where phoneNumber in
        defaultCitizenFiltering(
            "phoneNumber.in=" + DEFAULT_PHONE_NUMBER + "," + UPDATED_PHONE_NUMBER,
            "phoneNumber.in=" + UPDATED_PHONE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllCitizensByPhoneNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        citizenRepository.saveAndFlush(citizen);

        // Get all the citizenList where phoneNumber is not null
        defaultCitizenFiltering("phoneNumber.specified=true", "phoneNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllCitizensByPhoneNumberContainsSomething() throws Exception {
        // Initialize the database
        citizenRepository.saveAndFlush(citizen);

        // Get all the citizenList where phoneNumber contains
        defaultCitizenFiltering("phoneNumber.contains=" + DEFAULT_PHONE_NUMBER, "phoneNumber.contains=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllCitizensByPhoneNumberNotContainsSomething() throws Exception {
        // Initialize the database
        citizenRepository.saveAndFlush(citizen);

        // Get all the citizenList where phoneNumber does not contain
        defaultCitizenFiltering("phoneNumber.doesNotContain=" + UPDATED_PHONE_NUMBER, "phoneNumber.doesNotContain=" + DEFAULT_PHONE_NUMBER);
    }

    private void defaultCitizenFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCitizenShouldBeFound(shouldBeFound);
        defaultCitizenShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCitizenShouldBeFound(String filter) throws Exception {
        restCitizenMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(citizen.getId().intValue())))
            .andExpect(jsonPath("$.[*].nationalId").value(hasItem(DEFAULT_NATIONAL_ID)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)));

        // Check, that the count call also returns 1
        restCitizenMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCitizenShouldNotBeFound(String filter) throws Exception {
        restCitizenMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCitizenMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCitizen() throws Exception {
        // Get the citizen
        restCitizenMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCitizen() throws Exception {
        // Initialize the database
        citizenRepository.saveAndFlush(citizen);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        citizenSearchRepository.save(citizen);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citizenSearchRepository.findAll());

        // Update the citizen
        Citizen updatedCitizen = citizenRepository.findById(citizen.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCitizen are not directly saved in db
        em.detach(updatedCitizen);
        updatedCitizen
            .nationalId(UPDATED_NATIONAL_ID)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .phoneNumber(UPDATED_PHONE_NUMBER);
        CitizenDTO citizenDTO = citizenMapper.toDto(updatedCitizen);

        restCitizenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, citizenDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(citizenDTO))
            )
            .andExpect(status().isOk());

        // Validate the Citizen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCitizenToMatchAllProperties(updatedCitizen);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(citizenSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Citizen> citizenSearchList = Streamable.of(citizenSearchRepository.findAll()).toList();
                Citizen testCitizenSearch = citizenSearchList.get(searchDatabaseSizeAfter - 1);

                assertCitizenAllPropertiesEquals(testCitizenSearch, updatedCitizen);
            });
    }

    @Test
    @Transactional
    void putNonExistingCitizen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citizenSearchRepository.findAll());
        citizen.setId(longCount.incrementAndGet());

        // Create the Citizen
        CitizenDTO citizenDTO = citizenMapper.toDto(citizen);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCitizenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, citizenDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(citizenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Citizen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citizenSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCitizen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citizenSearchRepository.findAll());
        citizen.setId(longCount.incrementAndGet());

        // Create the Citizen
        CitizenDTO citizenDTO = citizenMapper.toDto(citizen);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCitizenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(citizenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Citizen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citizenSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCitizen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citizenSearchRepository.findAll());
        citizen.setId(longCount.incrementAndGet());

        // Create the Citizen
        CitizenDTO citizenDTO = citizenMapper.toDto(citizen);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCitizenMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(citizenDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Citizen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citizenSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCitizenWithPatch() throws Exception {
        // Initialize the database
        citizenRepository.saveAndFlush(citizen);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the citizen using partial update
        Citizen partialUpdatedCitizen = new Citizen();
        partialUpdatedCitizen.setId(citizen.getId());

        partialUpdatedCitizen.nationalId(UPDATED_NATIONAL_ID).firstName(UPDATED_FIRST_NAME).phoneNumber(UPDATED_PHONE_NUMBER);

        restCitizenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCitizen.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCitizen))
            )
            .andExpect(status().isOk());

        // Validate the Citizen in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCitizenUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCitizen, citizen), getPersistedCitizen(citizen));
    }

    @Test
    @Transactional
    void fullUpdateCitizenWithPatch() throws Exception {
        // Initialize the database
        citizenRepository.saveAndFlush(citizen);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the citizen using partial update
        Citizen partialUpdatedCitizen = new Citizen();
        partialUpdatedCitizen.setId(citizen.getId());

        partialUpdatedCitizen
            .nationalId(UPDATED_NATIONAL_ID)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .phoneNumber(UPDATED_PHONE_NUMBER);

        restCitizenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCitizen.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCitizen))
            )
            .andExpect(status().isOk());

        // Validate the Citizen in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCitizenUpdatableFieldsEquals(partialUpdatedCitizen, getPersistedCitizen(partialUpdatedCitizen));
    }

    @Test
    @Transactional
    void patchNonExistingCitizen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citizenSearchRepository.findAll());
        citizen.setId(longCount.incrementAndGet());

        // Create the Citizen
        CitizenDTO citizenDTO = citizenMapper.toDto(citizen);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCitizenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, citizenDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(citizenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Citizen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citizenSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCitizen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citizenSearchRepository.findAll());
        citizen.setId(longCount.incrementAndGet());

        // Create the Citizen
        CitizenDTO citizenDTO = citizenMapper.toDto(citizen);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCitizenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(citizenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Citizen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citizenSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCitizen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citizenSearchRepository.findAll());
        citizen.setId(longCount.incrementAndGet());

        // Create the Citizen
        CitizenDTO citizenDTO = citizenMapper.toDto(citizen);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCitizenMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(citizenDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Citizen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citizenSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCitizen() throws Exception {
        // Initialize the database
        citizenRepository.saveAndFlush(citizen);
        citizenRepository.save(citizen);
        citizenSearchRepository.save(citizen);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citizenSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the citizen
        restCitizenMockMvc
            .perform(delete(ENTITY_API_URL_ID, citizen.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citizenSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCitizen() throws Exception {
        // Initialize the database
        citizen = citizenRepository.saveAndFlush(citizen);
        citizenSearchRepository.save(citizen);

        // Search the citizen
        restCitizenMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + citizen.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(citizen.getId().intValue())))
            .andExpect(jsonPath("$.[*].nationalId").value(hasItem(DEFAULT_NATIONAL_ID)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)));
    }

    protected long getRepositoryCount() {
        return citizenRepository.count();
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

    protected Citizen getPersistedCitizen(Citizen citizen) {
        return citizenRepository.findById(citizen.getId()).orElseThrow();
    }

    protected void assertPersistedCitizenToMatchAllProperties(Citizen expectedCitizen) {
        assertCitizenAllPropertiesEquals(expectedCitizen, getPersistedCitizen(expectedCitizen));
    }

    protected void assertPersistedCitizenToMatchUpdatableProperties(Citizen expectedCitizen) {
        assertCitizenAllUpdatablePropertiesEquals(expectedCitizen, getPersistedCitizen(expectedCitizen));
    }
}
