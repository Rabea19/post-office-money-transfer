package io.fciz.postoffice.moneytransfer.domain;

import static io.fciz.postoffice.moneytransfer.domain.PostOfficeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.fciz.postoffice.moneytransfer.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PostOfficeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PostOffice.class);
        PostOffice postOffice1 = getPostOfficeSample1();
        PostOffice postOffice2 = new PostOffice();
        assertThat(postOffice1).isNotEqualTo(postOffice2);

        postOffice2.setId(postOffice1.getId());
        assertThat(postOffice1).isEqualTo(postOffice2);

        postOffice2 = getPostOfficeSample2();
        assertThat(postOffice1).isNotEqualTo(postOffice2);
    }
}
