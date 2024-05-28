package io.fciz.postoffice.moneytransfer.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.fciz.postoffice.moneytransfer.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CitizenDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CitizenDTO.class);
        CitizenDTO citizenDTO1 = new CitizenDTO();
        citizenDTO1.setId(1L);
        CitizenDTO citizenDTO2 = new CitizenDTO();
        assertThat(citizenDTO1).isNotEqualTo(citizenDTO2);
        citizenDTO2.setId(citizenDTO1.getId());
        assertThat(citizenDTO1).isEqualTo(citizenDTO2);
        citizenDTO2.setId(2L);
        assertThat(citizenDTO1).isNotEqualTo(citizenDTO2);
        citizenDTO1.setId(null);
        assertThat(citizenDTO1).isNotEqualTo(citizenDTO2);
    }
}
