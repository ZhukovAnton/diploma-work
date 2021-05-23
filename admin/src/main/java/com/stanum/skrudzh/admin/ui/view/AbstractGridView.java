package com.stanum.skrudzh.admin.ui.view;

import com.stanum.skrudzh.jpa.model.base.Base;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterObserver;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

@Slf4j
public abstract class AbstractGridView<T extends Base> extends VerticalLayout implements BeforeEnterObserver {
    protected ListDataProvider<T> dataProvider;
    protected HeaderRow filterRow;
    protected Grid<T> grid;
    protected JpaRepository<T, Long> repository;
    private final Class<T> clazz;

    @Value("${threebaskets.core_port}")
    private String port;

    public AbstractGridView(JpaRepository<T, Long> repository, Class<T> clazz) {
        this.repository = repository;
        this.grid = new Grid<>(clazz);
        List<T> templates = repository.findAll();
        this.dataProvider = new ListDataProvider<>(templates);
        this.filterRow = grid.appendHeaderRow();
        this.clazz = clazz;
    }

    protected void configureGrid() {
        grid.setDataProvider(dataProvider);
        grid.setRowsDraggable(true);
        grid.setColumnReorderingAllowed(true);
        grid.setSizeFull();
        grid.setHeight("1000px");
        grid.getColumns().forEach(c -> c.setResizable(true));
        grid.getColumns().forEach(c -> c.setAutoWidth(true));
        grid.setDropMode(GridDropMode.BETWEEN);


        setColumns();
        addFilters();

        Binder<T> binder = new Binder<>(clazz);
        Editor<T> editor = grid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        editFields(binder);

        Collection<Button> editButtons = Collections
                .newSetFromMap(new HashMap<>());

        Grid.Column<T> editorColumn = grid.addComponentColumn(v -> {
            Button edit = new Button("Edit");
            edit.addClassName("edit");
            edit.addClickListener(e -> {
                editor.editItem(v);
            });
            edit.setEnabled(!editor.isOpen());
            editButtons.add(edit);
            return edit;
        });

        this.grid.addComponentColumn(item -> new Button("Delete", click -> {
            deleteItem(item);
            grid.getDataProvider().refreshAll();
        }));

        editor.addOpenListener(e -> editButtons
                .forEach(button -> button.setEnabled(!editor.isOpen())));
        editor.addCloseListener(e -> editButtons
                .forEach(button -> button.setEnabled(!editor.isOpen())));

        Button save = new Button("Save", e -> {

            editor.save();
        });
        save.addClassName("save");

        Button cancel = new Button("Cancel", e -> editor.cancel());
        cancel.addClassName("cancel");

        grid.getElement().addEventListener("keyup", event -> editor.cancel())
                .setFilter("event.key === 'Escape' || event.key === 'Esc'");

        Div buttons = new Div(save, cancel);
        editorColumn.setEditorComponent(buttons);

        editor.addSaveListener(
                event -> {
                    updateValue(event.getItem());
                });

        Button addNewRowButton = new Button(
                "Add");
        addNewRowButton.addClickListener(
                buttonClickEvent -> {
                    dataProvider.getItems().add(newItem());
                    dataProvider.refreshAll();
                });

        Button saveButton = new Button(
                "Save");
        saveButton.addClickListener(
                buttonClickEvent -> saveNewRows());

        add(grid);
        add(addNewRowButton);
        add(saveButton);
    }

    private void saveNewRows() {
        dataProvider.getItems().forEach(
                i -> {
                    if(i.getId() == null) {
                        log.info("Save new item {}", i);
                        create(i);
                        afterCreate(i);
                    }
                }
        );
    }

    protected abstract T newItem();

    protected abstract void setColumns();

    protected abstract void addFilters();

    protected void addFilter(Function<T, String> function, String column) {
        TextField textField = new TextField();
        textField.addValueChangeListener(event -> dataProvider.addFilter(
                t -> {
                    String filter = textField.getValue();
                    if (filter == null || filter.isEmpty()) {
                        return true;
                    } else {
                        String value = function.apply(t);
                        return value != null && value.contains(filter);
                    }
                }));

        textField.setValueChangeMode(ValueChangeMode.EAGER);

        filterRow.getCell(grid.getColumnByKey(column)).setComponent(textField);
        textField.setSizeFull();
        textField.setPlaceholder("Filter");
    }

    protected void editField(Binder<T> binder, String name) {
        TextField nameField = new TextField();
        binder.forField(nameField).bind(name);
        grid.getColumnByKey(name).setEditorComponent(nameField);
    }

    protected void afterUpdate(T t) {

    }

    protected void afterCreate(T t) {

    }

    protected abstract void editFields(Binder<T> binder);

    protected abstract void create(T t);

    protected abstract void update(T oldValue, T newValue);

    protected abstract void deleteItem(T t);

    protected void reloadCache() {
        try {
            String uri = "http://127.0.0.1:" + port + "/localized_values";
            log.info("Send Reload cache request {}", uri);
            HttpGet request = new HttpGet(uri);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(request);
            log.info("Reload cache response: {}", response);
        } catch (IOException e) {
            log.error("Error while reloading cache", e);
        }
    }

    private void updateValue(T newEntity) {
        log.info("Update template {}", newEntity);
        if (newEntity == null || newEntity.getId() == null) {
            return;
        }

        T oldEntity = repository.findById(newEntity.getId()).get();
        update(oldEntity, newEntity);
        afterUpdate(oldEntity);
    }
}
