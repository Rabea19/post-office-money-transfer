package io.fciz.postoffice.moneytransfer.service.criteria;

import io.fciz.postoffice.moneytransfer.domain.enumeration.TransactionStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.fciz.postoffice.moneytransfer.domain.Transaction} entity. This class is used
 * in {@link io.fciz.postoffice.moneytransfer.web.rest.TransactionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /transactions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TransactionStatus
     */
    public static class TransactionStatusFilter extends Filter<TransactionStatus> {

        public TransactionStatusFilter() {}

        public TransactionStatusFilter(TransactionStatusFilter filter) {
            super(filter);
        }

        @Override
        public TransactionStatusFilter copy() {
            return new TransactionStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter amount;

    private LocalDateFilter transactionDate;

    private TransactionStatusFilter status;

    private LongFilter senderId;

    private LongFilter receiverId;

    private LongFilter senderPostOfficeId;

    private LongFilter receiverPostOfficeId;

    private Boolean distinct;

    public TransactionCriteria() {}

    public TransactionCriteria(TransactionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.amount = other.optionalAmount().map(BigDecimalFilter::copy).orElse(null);
        this.transactionDate = other.optionalTransactionDate().map(LocalDateFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(TransactionStatusFilter::copy).orElse(null);
        this.senderId = other.optionalSenderId().map(LongFilter::copy).orElse(null);
        this.receiverId = other.optionalReceiverId().map(LongFilter::copy).orElse(null);
        this.senderPostOfficeId = other.optionalSenderPostOfficeId().map(LongFilter::copy).orElse(null);
        this.receiverPostOfficeId = other.optionalReceiverPostOfficeId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TransactionCriteria copy() {
        return new TransactionCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public BigDecimalFilter getAmount() {
        return amount;
    }

    public Optional<BigDecimalFilter> optionalAmount() {
        return Optional.ofNullable(amount);
    }

    public BigDecimalFilter amount() {
        if (amount == null) {
            setAmount(new BigDecimalFilter());
        }
        return amount;
    }

    public void setAmount(BigDecimalFilter amount) {
        this.amount = amount;
    }

    public LocalDateFilter getTransactionDate() {
        return transactionDate;
    }

    public Optional<LocalDateFilter> optionalTransactionDate() {
        return Optional.ofNullable(transactionDate);
    }

    public LocalDateFilter transactionDate() {
        if (transactionDate == null) {
            setTransactionDate(new LocalDateFilter());
        }
        return transactionDate;
    }

    public void setTransactionDate(LocalDateFilter transactionDate) {
        this.transactionDate = transactionDate;
    }

    public TransactionStatusFilter getStatus() {
        return status;
    }

    public Optional<TransactionStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public TransactionStatusFilter status() {
        if (status == null) {
            setStatus(new TransactionStatusFilter());
        }
        return status;
    }

    public void setStatus(TransactionStatusFilter status) {
        this.status = status;
    }

    public LongFilter getSenderId() {
        return senderId;
    }

    public Optional<LongFilter> optionalSenderId() {
        return Optional.ofNullable(senderId);
    }

    public LongFilter senderId() {
        if (senderId == null) {
            setSenderId(new LongFilter());
        }
        return senderId;
    }

    public void setSenderId(LongFilter senderId) {
        this.senderId = senderId;
    }

    public LongFilter getReceiverId() {
        return receiverId;
    }

    public Optional<LongFilter> optionalReceiverId() {
        return Optional.ofNullable(receiverId);
    }

    public LongFilter receiverId() {
        if (receiverId == null) {
            setReceiverId(new LongFilter());
        }
        return receiverId;
    }

    public void setReceiverId(LongFilter receiverId) {
        this.receiverId = receiverId;
    }

    public LongFilter getSenderPostOfficeId() {
        return senderPostOfficeId;
    }

    public Optional<LongFilter> optionalSenderPostOfficeId() {
        return Optional.ofNullable(senderPostOfficeId);
    }

    public LongFilter senderPostOfficeId() {
        if (senderPostOfficeId == null) {
            setSenderPostOfficeId(new LongFilter());
        }
        return senderPostOfficeId;
    }

    public void setSenderPostOfficeId(LongFilter senderPostOfficeId) {
        this.senderPostOfficeId = senderPostOfficeId;
    }

    public LongFilter getReceiverPostOfficeId() {
        return receiverPostOfficeId;
    }

    public Optional<LongFilter> optionalReceiverPostOfficeId() {
        return Optional.ofNullable(receiverPostOfficeId);
    }

    public LongFilter receiverPostOfficeId() {
        if (receiverPostOfficeId == null) {
            setReceiverPostOfficeId(new LongFilter());
        }
        return receiverPostOfficeId;
    }

    public void setReceiverPostOfficeId(LongFilter receiverPostOfficeId) {
        this.receiverPostOfficeId = receiverPostOfficeId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TransactionCriteria that = (TransactionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(transactionDate, that.transactionDate) &&
            Objects.equals(status, that.status) &&
            Objects.equals(senderId, that.senderId) &&
            Objects.equals(receiverId, that.receiverId) &&
            Objects.equals(senderPostOfficeId, that.senderPostOfficeId) &&
            Objects.equals(receiverPostOfficeId, that.receiverPostOfficeId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, transactionDate, status, senderId, receiverId, senderPostOfficeId, receiverPostOfficeId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalAmount().map(f -> "amount=" + f + ", ").orElse("") +
            optionalTransactionDate().map(f -> "transactionDate=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalSenderId().map(f -> "senderId=" + f + ", ").orElse("") +
            optionalReceiverId().map(f -> "receiverId=" + f + ", ").orElse("") +
            optionalSenderPostOfficeId().map(f -> "senderPostOfficeId=" + f + ", ").orElse("") +
            optionalReceiverPostOfficeId().map(f -> "receiverPostOfficeId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
