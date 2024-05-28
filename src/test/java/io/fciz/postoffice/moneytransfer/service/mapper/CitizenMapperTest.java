package io.fciz.postoffice.moneytransfer.service.mapper;

import static io.fciz.postoffice.moneytransfer.domain.CitizenAsserts.*;
import static io.fciz.postoffice.moneytransfer.domain.CitizenTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CitizenMapperTest {

    private CitizenMapper citizenMapper;

    @BeforeEach
    void setUp() {
        citizenMapper = new CitizenMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCitizenSample1();
        var actual = citizenMapper.toEntity(citizenMapper.toDto(expected));
        assertCitizenAllPropertiesEquals(expected, actual);
    }
}
