package io.fciz.postoffice.moneytransfer.domain;

import io.fciz.postoffice.moneytransfer.domain.enumeration.TransactionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A Transaction.
 */
@Entity
@Table(name = "transaction")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "transaction")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @NotNull
    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private TransactionStatus status;

    @ManyToOne(optional = false)
    @NotNull
    private Citizen sender;

    @ManyToOne(optional = false)
    @NotNull
    private Citizen receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    private PostOffice senderPostOffice;

    @ManyToOne(fetch = FetchType.LAZY)
    private PostOffice receiverPostOffice;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Transaction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Transaction amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getTransactionDate() {
        return this.transactionDate;
    }

    public Transaction transactionDate(LocalDate transactionDate) {
        this.setTransactionDate(transactionDate);
        return this;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public TransactionStatus getStatus() {
        return this.status;
    }

    public Transaction status(TransactionStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Citizen getSender() {
        return this.sender;
    }

    public void setSender(Citizen citizen) {
        this.sender = citizen;
    }

    public Transaction sender(Citizen citizen) {
        this.setSender(citizen);
        return this;
    }

    public Citizen getReceiver() {
        return this.receiver;
    }

    public void setReceiver(Citizen citizen) {
        this.receiver = citizen;
    }

    public Transaction receiver(Citizen citizen) {
        this.setReceiver(citizen);
        return this;
    }

    public PostOffice getSenderPostOffice() {
        return this.senderPostOffice;
    }

    public void setSenderPostOffice(PostOffice postOffice) {
        this.senderPostOffice = postOffice;
    }

    public Transaction senderPostOffice(PostOffice postOffice) {
        this.setSenderPostOffice(postOffice);
        return this;
    }

    public PostOffice getReceiverPostOffice() {
        return this.receiverPostOffice;
    }

    public void setReceiverPostOffice(PostOffice postOffice) {
        this.receiverPostOffice = postOffice;
    }

    public Transaction receiverPostOffice(PostOffice postOffice) {
        this.setReceiverPostOffice(postOffice);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transaction)) {
            return false;
        }
        return getId() != null && getId().equals(((Transaction) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Transaction{" +
            "id=" + getId() +
            ", amount=" + getAmount() +
            ", transactionDate='" + getTransactionDate() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
