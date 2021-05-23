package com.stanum.skrudzh.admin.ui;

import com.stanum.skrudzh.admin.ui.view.LocalizedView;
import com.stanum.skrudzh.admin.ui.view.TemplateView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;

@CssImport("./styles/shared-styles.css")
public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Capitalist");
        logo.addClassName("logo");

        Anchor logout = new Anchor("/logout", "Log out    ");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, logout);
        header.addClassName("header");
        header.setWidth("100%");
        header.expand(logo);
        header.setDefaultVerticalComponentAlignment(
                FlexComponent.Alignment.CENTER);

        addToNavbar(header);
    }

    private void createDrawer() {
        RouterLink templates = new RouterLink("Templates", TemplateView.class);
        templates.setHighlightCondition(HighlightConditions.sameLocation());

        RouterLink localized = new RouterLink("Localized", LocalizedView.class);
        localized.setHighlightCondition(HighlightConditions.sameLocation());

        addToDrawer(new VerticalLayout(templates));
        addToDrawer(new VerticalLayout(localized));
    }
}