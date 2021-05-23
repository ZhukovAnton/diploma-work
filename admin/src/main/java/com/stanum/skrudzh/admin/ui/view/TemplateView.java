package com.stanum.skrudzh.admin.ui.view;

import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.stanum.skrudzh.admin.ui.MainLayout;
import com.stanum.skrudzh.jpa.model.TransactionableExampleEntity;
import com.stanum.skrudzh.jpa.model.system.LocalizedValue;
import com.stanum.skrudzh.jpa.repository.LocalizedValuesRepository;
import com.stanum.skrudzh.jpa.repository.TransactionableExampleRepository;
import com.stanum.skrudzh.model.enums.MessageType;
import com.stanum.skrudzh.utils.TimeUtil;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@Route(value = "templates", layout = MainLayout.class)
@PageTitle("Templates | Capitalist")
@Slf4j
public class TemplateView extends AbstractGridView<TransactionableExampleEntity> {

    @Autowired
    private LocalizedValuesRepository localizedValuesRepository;

    public TemplateView(TransactionableExampleRepository repository) {
        super(repository, TransactionableExampleEntity.class);
        configureGrid();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

    }

    @Override
    protected void setColumns() {
        grid.setColumns("id", "name", "iconUrl", "transactionableType", "prototypeKey", "country", "rowOrder");
    }

    @Override
    protected void addFilters() {
        addFilter(TransactionableExampleEntity::getTransactionableType, "transactionableType");
        addFilter(TransactionableExampleEntity::getCountry, "country");
    }

    @Override
    protected TransactionableExampleEntity newItem() {
        return new TransactionableExampleEntity();
    }

    @Override
    protected void editFields(Binder<TransactionableExampleEntity> binder) {
        editField(binder, "name");
        editField(binder, "iconUrl");
        editField(binder, "prototypeKey");
        editField(binder, "country");
        editField(binder, "transactionableType");

        TextField nameField = new TextField();
        binder.forField(nameField).withNullRepresentation("0").withConverter(
                new StringToIntegerConverter(0, "Error while converting row order"))
                .bind("rowOrder");
        grid.getColumnByKey("rowOrder").setEditorComponent(nameField);
    }

    @Override
    protected void update(TransactionableExampleEntity oldValue, TransactionableExampleEntity newValue) {
        oldValue.setUpdatedAt(TimeUtil.now());
        oldValue.setNameKey("activerecord.defaults.models.transactionable_example."
                + formatCategory(oldValue.getTransactionableType()) + ".attributes.name." +
                (oldValue.getPrototypeKey() == null ? "" : oldValue.getPrototypeKey()));
        if (newValue.getRowOrder() == null) {
            oldValue.setRowOrder(0);
        } else {
            oldValue.setRowOrder(newValue.getRowOrder());
        }
        if(Strings.isNullOrEmpty(newValue.getCountry())) {
            oldValue.setCountry(null);
        } else {
            oldValue.setCountry(newValue.getCountry());
        }
        if(Strings.isNullOrEmpty(newValue.getPrototypeKey())) {
            oldValue.setPrototypeKey(null);
        } else {
            oldValue.setPrototypeKey(newValue.getPrototypeKey());
        }

        oldValue.setName(newValue.getName());
        oldValue.setIconUrl(newValue.getIconUrl());
        oldValue.setTransactionableType(newValue.getTransactionableType());

        log.info("Save tr example {}", oldValue);
        repository.save(oldValue);
    }

    @Override
    protected void create(TransactionableExampleEntity entity) {
        entity.setCreatedAt(TimeUtil.now());
        entity.setUpdatedAt(TimeUtil.now());
        entity.setCreateByDefault(false);
        entity.setNameKey("activerecord.defaults.models.transactionable_example."
                + formatCategory(entity.getTransactionableType()) + ".attributes.name." +
                (entity.getPrototypeKey() == null ? "" : entity.getPrototypeKey()));
        if (entity.getRowOrder() == null) {
            entity.setRowOrder(0);
        }
        if("".equals(entity.getCountry())) {
            entity.setCountry(null);
        }
        log.info("Save tr example {}", entity);
        repository.save(entity);
    }

    @Override
    protected void afterCreate(TransactionableExampleEntity entity) {
        LocalizedValue l = new LocalizedValue();
        l.setCreatedAt(TimeUtil.now());
        l.setUpdatedAt(TimeUtil.now());

        if (entity.getCountry() != null && entity.getCountry().contains("RU")) {
            l.setLocale("ru");
        } else {
            l.setLocale("en");
        }
        l.setType(MessageType.transactionable_example);
        l.setKey(entity.getNameKey());
        l.setValue(entity.getName());

        log.info("Save localized value {}", l);
        localizedValuesRepository.save(l);
        reloadCache();
    }

    @Override
    protected void afterUpdate(TransactionableExampleEntity entity) {
        reloadCache();
    }

    @Override
    protected void deleteItem(TransactionableExampleEntity e) {
        if (e.getId() != null) {
            repository.deleteById(e.getId());
            dataProvider.getItems().removeIf(i -> e.getId().equals(i.getId()));
        } else {
            dataProvider.getItems().removeIf(ex -> ex.getId() == null
                    && (Objects.equals(e.getName(), ex.getName()))
                    && (Objects.equals(e.getIconUrl(), ex.getIconUrl()))
                    && (Objects.equals(e.getCountry(), ex.getCountry()))
                    && (Objects.equals(e.getTransactionableType(), ex.getTransactionableType()))
                    && (Objects.equals(e.getPrototypeKey(), ex.getPrototypeKey())));
        }
    }

    private String formatCategory(String str) {
        if (str == null) {
            return "";
        }
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, str);
    }
}
