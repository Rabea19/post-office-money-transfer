package io.fciz.postoffice.moneytransfer.domain;

import static io.fciz.postoffice.moneytransfer.domain.AssertUtils.bigDecimalCompareTo;
import static org.assertj.core.api.Assertions.assertThat;

public class TransactionAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTransactionAllPropertiesEquals(Transaction expected, Transaction actual) {
        assertTransactionAutoGeneratedPropertiesEquals(expected, actual);
        assertTransactionAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTransactionAllUpdatablePropertiesEquals(Transaction expected, Transaction actual) {
        assertTransactionUpdatableFieldsEquals(expected, actual);
        assertTransactionUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTransactionAutoGeneratedPropertiesEquals(Transaction expected, Transaction actual) {
        assertThat(expected)
            .as("Verify Transaction auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTransactionUpdatableFieldsEquals(Transaction expected, Transaction actual) {
        assertThat(expected)
            .as("Verify Transaction relevant properties")
            .satisfies(e -> assertThat(e.getAmount()).as("check amount").usingComparator(bigDecimalCompareTo).isEqualTo(actual.getAmount()))
            .satisfies(e -> assertThat(e.getTransactionDate()).as("check transactionDate").isEqualTo(actual.getTransactionDate()))
            .satisfies(e -> assertThat(e.getStatus()).as("check status").isEqualTo(actual.getStatus()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTransactionUpdatableRelationshipsEquals(Transaction expected, Transaction actual) {
        assertThat(expected)
            .as("Verify Transaction relationships")
            .satisfies(e -> assertThat(e.getSender()).as("check sender").isEqualTo(actual.getSender()))
            .satisfies(e -> assertThat(e.getReceiver()).as("check receiver").isEqualTo(actual.getReceiver()))
            .satisfies(e -> assertThat(e.getSenderPostOffice()).as("check senderPostOffice").isEqualTo(actual.getSenderPostOffice()))
            .satisfies(e -> assertThat(e.getReceiverPostOffice()).as("check receiverPostOffice").isEqualTo(actual.getReceiverPostOffice()));
    }
}
