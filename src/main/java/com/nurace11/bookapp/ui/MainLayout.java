package com.nurace11.bookapp.ui;

import com.nurace11.bookapp.ui.view.author.AuthorsView;
import com.nurace11.bookapp.ui.view.book.BooksView;
import com.nurace11.bookapp.ui.view.landing.LandingView;
import com.nurace11.bookapp.ui.view.report.ReportView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        RouterLink logoLink = new RouterLink(LandingView.class);
        logoLink.add(new H1("BookApp"));
        logoLink.setHighlightCondition(HighlightConditions.sameLocation());

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logoLink);
        header.setWidth("100%");
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        addToNavbar(header);
    }

    private void createDrawer() {
        addToDrawer(new VerticalLayout(
                new RouterLink("Книги", BooksView.class),
                new RouterLink("Авторы", AuthorsView.class),
                new RouterLink("Отчеты", ReportView.class)
        ));
    }
}
