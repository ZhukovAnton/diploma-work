package com.stanum.skrudzh.model.dto;

import com.stanum.skrudzh.jpa.model.TransactionableExampleEntity;
import com.stanum.skrudzh.model.dto.base.Ordered;
import com.stanum.skrudzh.utils.RequestUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.ResourceBundle;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Slf4j
public class TransactionableExample extends Ordered {

    public TransactionableExample(TransactionableExampleEntity transactionableExampleEntity) {
        this.id = transactionableExampleEntity.getId();
        this.name = transactionableExampleEntity.getName();
        this.localizedKey = transactionableExampleEntity.getNameKey();
        this.iconUrl = transactionableExampleEntity.getIconUrl();
        this.transactionableType = transactionableExampleEntity.getTransactionableType();
        this.basketType = transactionableExampleEntity.getBasketType() != null
                ? transactionableExampleEntity.getBasketType().toString()
                : null;
        this.descriptionKey = transactionableExampleEntity.getDescriptionKey();

        this.prototypeKey = transactionableExampleEntity.getPrototypeKey();
        this.rowOrder = transactionableExampleEntity.getRowOrder();
    }

    private Long id;

    private String name;

    private String localizedKey;

    private String localizedName;

    private String iconUrl;

    private String transactionableType;

    private String basketType;

    private String descriptionKey;

    private String localizedDescription;

    private String prototypeKey;

    private List<String> providerCodes;

}
