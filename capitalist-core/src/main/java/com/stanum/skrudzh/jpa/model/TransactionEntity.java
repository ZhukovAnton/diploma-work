package com.stanum.skrudzh.jpa.model;

import com.stanum.skrudzh.jpa.model.base.Base;
import com.stanum.skrudzh.jpa.model.base.HasUser;
import com.stanum.skrudzh.model.enums.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "transactions")
@EqualsAndHashCode
@Data
public class TransactionEntity implements Base, HasUser, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "source_type")
    private String sourceType;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "destination_type")
    private String destinationType;

    @Column(name = "destination_id")
    private Long destinationId;

    @Column(name = "source_title")
    private String sourceTitle;

    @Column(name = "destination_title")
    private String destinationTitle;

    @Column(name = "destination_icon_url")
    private String destinationIconUrl;

    @Column(name = "transaction_type")
    @Enumerated
    private TransactionTypeEnum transactionType;

    @Basic
    @Column(name = "got_at")
    private Timestamp gotAt;

    @Basic
    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @Column(name = "amount_cents")
    private BigDecimal amountCents;

    @Column(name = "amount_currency")
    private String amountCurrency;

    @Column(name = "converted_amount_cents")
    private BigDecimal convertedAmountCents;

    @Column(name = "converted_amount_currency")
    private String convertedAmountCurrency;

    @Column(name = "comment")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "whom")
    private String whom;

    @Basic
    @Column(name = "payday")
    private Timestamp payday;

    @Enumerated
    @Column(name = "borrow_type")
    private BorrowTypeEnum borrowType;

    @Column(name = "is_returned")
    private Boolean isReturned;

    @OneToOne
    @JoinColumn(name = "borrow_id")
    private BorrowEntity borrow;

    @ManyToOne
    @JoinColumn(name = "returning_borrow_id")
    private BorrowEntity returningBorrow;

    @Enumerated
    @Column(name = "basket_type")
    private BasketTypeEnum basketType;

    @Column(name = "buying_asset")
    private Boolean buyingAsset = false;

    @OneToOne
    @JoinColumn(name = "credit_id")
    private CreditEntity credit;

    @Column(name = "source_icon_url")
    private String sourceIconUrl;

    @Column(name = "salt_edge_transaction_id")
    private String saltEdgeTransactionId;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountEntity accountEntity;

    @OneToOne
    @JoinColumn(name = "active_id")
    private ActiveEntity activeEntity;

    @Column(name = "is_virtual_source")
    private Boolean isVirtualSource = false;

    @Column(name = "is_virtual_destination")
    private Boolean isVirtualDestination = false;

    @Column(name = "is_borrow_or_return_source")
    private Boolean isBorrowOrReturnSource = false;

    @Column(name = "is_borrow_or_return_destination")
    private Boolean isBorrowOrReturnDestination = false;

    @Column(name = "is_active_source")
    private Boolean isActiveSource = false;

    @Column(name = "profit")
    private BigDecimal profit;

    @Enumerated
    @Column(name = "salt_edge_transaction_status")
    private SaltEdgeTransactionStatusEnum saltEdgeTransactionStatus;

    @Column(name = "is_duplicated")
    private Boolean isDuplicated = false;

    @Column(name = "is_duplication_actual")
    private Boolean isDuplicationActual = false;

    @Column(name = "is_changeable")
    private Boolean isChangeable = true;

    @Column(name = "is_auto_categorized")
    private Boolean isAutoCategorized = false;

    @Column(name = "salt_edge_category")
    private String saltEdgeCategory;

    @Enumerated
    @Column(name = "nature")
    private TransactionNatureEnum transactionNature = TransactionNatureEnum.user;

    @Enumerated
    @Column(name = "purpose")
    private TransactionPurposeEnum transactionPurpose = TransactionPurposeEnum.regular;

}
