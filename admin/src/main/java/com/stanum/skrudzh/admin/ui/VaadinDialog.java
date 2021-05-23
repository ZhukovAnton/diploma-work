package com.stanum.skrudzh.admin.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class VaadinDialog {

    public static final String SAVE_TEXT = "Are you sure you want to save the item?";
    public static final String DELETE_TEXT = "Are you sure you want to delete the item?";
    public static final String DELETE_TEXT_FOR_COURSE = "Are you sure you want to delete the course with its associated lessons, their parts, and phrases?";
    public static final String DELETE_TEXT_FOR_LESSON = "Are you sure you want to delete the lesson with its associated parts and phrases?";
    public static final String PUBLISH = "Are you sure you want to publish this course with all it's translations?";
    public static final String CONTENT = "Please, select a content language.";

    public static Dialog createDialog(Button actionButton, Button cancel, String text) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(true);
        dialog.setWidth("420px");
        dialog.setHeight("130px");
        dialog.setDraggable(true);
        dialog.add(text);
        dialog.add(new HorizontalLayout(actionButton, cancel));
        if (!text.equals(CONTENT)) {
            cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        }
        return dialog;
    }
}
