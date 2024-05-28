package io.fciz.postoffice.moneytransfer.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PostOfficeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PostOffice getPostOfficeSample1() {
        return new PostOffice()
            .id(1L)
            .name("name1")
            .streetAddress("streetAddress1")
            .city("city1")
            .state("state1")
            .postalCode("postalCode1");
    }

    public static PostOffice getPostOfficeSample2() {
        return new PostOffice()
            .id(2L)
            .name("name2")
            .streetAddress("streetAddress2")
            .city("city2")
            .state("state2")
            .postalCode("postalCode2");
    }

    public static PostOffice getPostOfficeRandomSampleGenerator() {
        return new PostOffice()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .streetAddress(UUID.randomUUID().toString())
            .city(UUID.randomUUID().toString())
            .state(UUID.randomUUID().toString())
            .postalCode(UUID.randomUUID().toString());
    }
}
