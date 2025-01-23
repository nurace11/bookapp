package com.nurace11.bookapp.ui.view.author;

import com.nurace11.bookapp.ui.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "authors", layout = MainLayout.class)
@PageTitle("Authors list | BookApp")
public class AuthorView extends VerticalLayout {

    public AuthorView() {
        add(new Text("AuthorList"));
    }
}
