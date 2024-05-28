package io.fciz.postoffice.moneytransfer.domain;

import static io.fciz.postoffice.moneytransfer.domain.CitizenTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.fciz.postoffice.moneytransfer.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CitizenTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Citizen.class);
        Citizen citizen1 = getCitizenSample1();
        Citizen citizen2 = new Citizen();
        assertThat(citizen1).isNotEqualTo(citizen2);

        citizen2.setId(citizen1.getId());
        assertThat(citizen1).isEqualTo(citizen2);

        citizen2 = getCitizenSample2();
        assertThat(citizen1).isNotEqualTo(citizen2);
    }
}
