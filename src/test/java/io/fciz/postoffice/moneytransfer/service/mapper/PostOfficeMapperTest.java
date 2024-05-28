package io.fciz.postoffice.moneytransfer.service.mapper;

import static io.fciz.postoffice.moneytransfer.domain.PostOfficeAsserts.*;
import static io.fciz.postoffice.moneytransfer.domain.PostOfficeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PostOfficeMapperTest {

    private PostOfficeMapper postOfficeMapper;

    @BeforeEach
    void setUp() {
        postOfficeMapper = new PostOfficeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPostOfficeSample1();
        var actual = postOfficeMapper.toEntity(postOfficeMapper.toDto(expected));
        assertPostOfficeAllPropertiesEquals(expected, actual);
    }
}
