package io.fciz.postoffice.moneytransfer.domain;

import static io.fciz.postoffice.moneytransfer.domain.CitizenTestSamples.*;
import static io.fciz.postoffice.moneytransfer.domain.PostOfficeTestSamples.*;
import static io.fciz.postoffice.moneytransfer.domain.TransactionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.fciz.postoffice.moneytransfer.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TransactionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Transaction.class);
        Transaction transaction1 = getTransactionSample1();
        Transaction transaction2 = new Transaction();
        assertThat(transaction1).isNotEqualTo(transaction2);

        transaction2.setId(transaction1.getId());
        assertThat(transaction1).isEqualTo(transaction2);

        transaction2 = getTransactionSample2();
        assertThat(transaction1).isNotEqualTo(transaction2);
    }

    @Test
    void senderTest() throws Exception {
        Transaction transaction = getTransactionRandomSampleGenerator();
        Citizen citizenBack = getCitizenRandomSampleGenerator();

        transaction.setSender(citizenBack);
        assertThat(transaction.getSender()).isEqualTo(citizenBack);

        transaction.sender(null);
        assertThat(transaction.getSender()).isNull();
    }

    @Test
    void receiverTest() throws Exception {
        Transaction transaction = getTransactionRandomSampleGenerator();
        Citizen citizenBack = getCitizenRandomSampleGenerator();

        transaction.setReceiver(citizenBack);
        assertThat(transaction.getReceiver()).isEqualTo(citizenBack);

        transaction.receiver(null);
        assertThat(transaction.getReceiver()).isNull();
    }

    @Test
    void senderPostOfficeTest() throws Exception {
        Transaction transaction = getTransactionRandomSampleGenerator();
        PostOffice postOfficeBack = getPostOfficeRandomSampleGenerator();

        transaction.setSenderPostOffice(postOfficeBack);
        assertThat(transaction.getSenderPostOffice()).isEqualTo(postOfficeBack);

        transaction.senderPostOffice(null);
        assertThat(transaction.getSenderPostOffice()).isNull();
    }

    @Test
    void receiverPostOfficeTest() throws Exception {
        Transaction transaction = getTransactionRandomSampleGenerator();
        PostOffice postOfficeBack = getPostOfficeRandomSampleGenerator();

        transaction.setReceiverPostOffice(postOfficeBack);
        assertThat(transaction.getReceiverPostOffice()).isEqualTo(postOfficeBack);

        transaction.receiverPostOffice(null);
        assertThat(transaction.getReceiverPostOffice()).isNull();
    }
}
