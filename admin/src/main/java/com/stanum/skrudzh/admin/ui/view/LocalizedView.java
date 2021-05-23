package com.stanum.skrudzh.admin.ui.view;

import com.stanum.skrudzh.admin.ui.MainLayout;
import com.stanum.skrudzh.jpa.model.system.LocalizedValue;
import com.stanum.skrudzh.jpa.repository.LocalizedValuesRepository;
import com.stanum.skrudzh.model.enums.MessageType;
import com.stanum.skrudzh.utils.TimeUtil;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import java.util.Objects;

@Route(value = "localized", layout = MainLayout.class)
@PageTitle("Localized Values | Capitalist")
@Slf4j
public class LocalizedView extends AbstractGridView<LocalizedValue> {

    public LocalizedView(LocalizedValuesRepository repository) {
        super(repository, LocalizedValue.class);
        configureGrid();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

    }

    @Override
    protected void editFields(Binder<LocalizedValue> binder) {
        editField(binder, "key");
        editField(binder, "value");
        editField(binder, "locale");

        TextField nameField = new TextField();
        binder.forField(nameField).withConverter(new Converter<String, MessageType>() {
                                                     @Override
                                                     public Result<MessageType> convertToModel(String value, ValueContext context) {
                                                         return Result.ok((value == null || value.isEmpty()) ? null : MessageType.valueOf(value));
                                                     }

                                                     @Override
                                                     public String convertToPresentation(MessageType value, ValueContext context) {
                                                         return value == null ? "" : value.name();
                                                     }
                                                 }
        ).bind("type");
        grid.getColumnByKey("type").setEditorComponent(nameField);
    }


    @Override
    protected void setColumns() {
        grid.setColumns("id", "type", "key", "value", "locale");
    }

    @Override
    protected LocalizedValue newItem() {
        return new LocalizedValue();
    }

    @Override
    protected void create(LocalizedValue localizedValue) {
        localizedValue.setCreatedAt(TimeUtil.now());
        localizedValue.setUpdatedAt(TimeUtil.now());
        repository.save(localizedValue);
        reloadCache();
    }

    @Override
    protected void update(LocalizedValue oldValue, LocalizedValue newValue) {
        oldValue.setUpdatedAt(TimeUtil.now());
        oldValue.setType(newValue.getType());
        oldValue.setKey(newValue.getKey());
        oldValue.setValue(newValue.getValue());
        oldValue.setLocale(newValue.getLocale());
        repository.save(oldValue);
        reloadCache();
    }

    @Override
    protected void deleteItem(LocalizedValue l) {
        if(l.getId() != null) {
            repository.deleteById(l.getId());
            dataProvider.getItems().removeIf(i -> l.getId().equals(i.getId()));
        } else {
            dataProvider.getItems().removeIf(localizedValue -> localizedValue.getId() == null
                    && (Objects.equals(l.getKey(), localizedValue.getKey()))
                    && (Objects.equals(l.getLocale(), localizedValue.getLocale()))
                    && (Objects.equals(l.getType(), localizedValue.getType())));
        }
    }

    @Override
    protected void addFilters() {
        addFilter(LocalizedValue::getKey, "key");
        addFilter(LocalizedValue::getLocale, "locale");
        addFilter(t -> t.getType().name(), "type");
    }
}