package io.fciz.postoffice.moneytransfer.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.fciz.postoffice.moneytransfer.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PostOfficeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PostOfficeDTO.class);
        PostOfficeDTO postOfficeDTO1 = new PostOfficeDTO();
        postOfficeDTO1.setId(1L);
        PostOfficeDTO postOfficeDTO2 = new PostOfficeDTO();
        assertThat(postOfficeDTO1).isNotEqualTo(postOfficeDTO2);
        postOfficeDTO2.setId(postOfficeDTO1.getId());
        assertThat(postOfficeDTO1).isEqualTo(postOfficeDTO2);
        postOfficeDTO2.setId(2L);
        assertThat(postOfficeDTO1).isNotEqualTo(postOfficeDTO2);
        postOfficeDTO1.setId(null);
        assertThat(postOfficeDTO1).isNotEqualTo(postOfficeDTO2);
    }
}
