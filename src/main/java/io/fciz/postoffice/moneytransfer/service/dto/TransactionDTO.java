package io.fciz.postoffice.moneytransfer.service.dto;

import io.fciz.postoffice.moneytransfer.domain.enumeration.TransactionStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link io.fciz.postoffice.moneytransfer.domain.Transaction} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionDTO implements Serializable {

    private Long id;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private LocalDate transactionDate;

    @NotNull
    private TransactionStatus status;

    @NotNull
    private CitizenDTO sender;

    @NotNull
    private CitizenDTO receiver;

    private PostOfficeDTO senderPostOffice;

    private PostOfficeDTO receiverPostOffice;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public CitizenDTO getSender() {
        return sender;
    }

    public void setSender(CitizenDTO sender) {
        this.sender = sender;
    }

    public CitizenDTO getReceiver() {
        return receiver;
    }

    public void setReceiver(CitizenDTO receiver) {
        this.receiver = receiver;
    }

    public PostOfficeDTO getSenderPostOffice() {
        return senderPostOffice;
    }

    public void setSenderPostOffice(PostOfficeDTO senderPostOffice) {
        this.senderPostOffice = senderPostOffice;
    }

    public PostOfficeDTO getReceiverPostOffice() {
        return receiverPostOffice;
    }

    public void setReceiverPostOffice(PostOfficeDTO receiverPostOffice) {
        this.receiverPostOffice = receiverPostOffice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionDTO)) {
            return false;
        }

        TransactionDTO transactionDTO = (TransactionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, transactionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionDTO{" +
            "id=" + getId() +
            ", amount=" + getAmount() +
            ", transactionDate='" + getTransactionDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", sender=" + getSender() +
            ", receiver=" + getReceiver() +
            ", senderPostOffice=" + getSenderPostOffice() +
            ", receiverPostOffice=" + getReceiverPostOffice() +
            "}";
    }
}
