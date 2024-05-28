package io.fciz.postoffice.moneytransfer.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CitizenTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Citizen getCitizenSample1() {
        return new Citizen().id(1L).nationalId("nationalId1").firstName("firstName1").lastName("lastName1").phoneNumber("phoneNumber1");
    }

    public static Citizen getCitizenSample2() {
        return new Citizen().id(2L).nationalId("nationalId2").firstName("firstName2").lastName("lastName2").phoneNumber("phoneNumber2");
    }

    public static Citizen getCitizenRandomSampleGenerator() {
        return new Citizen()
            .id(longCount.incrementAndGet())
            .nationalId(UUID.randomUUID().toString())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .phoneNumber(UUID.randomUUID().toString());
    }
}
