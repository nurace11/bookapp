package com.nurace11.bookapp.ui.view.author;

import com.nurace11.bookapp.model.AuthorModel;
import com.nurace11.bookapp.model.BookModel;
import com.nurace11.bookapp.service.AuthorService;
import com.nurace11.bookapp.ui.MainLayout;
import com.nurace11.bookapp.ui.view.book.BookView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Route(value = "authors", layout = MainLayout.class)
@PageTitle("Authors list | BookApp")
public class AuthorsView extends VerticalLayout {

    private final AuthorService authorService;

    private List<AuthorModel> authors = new ArrayList<>();
    private Grid<AuthorModel> grid;

    private Dialog createAuthorDialog;

    public AuthorsView(AuthorService authorService) {
        this.authorService = authorService;

        initDialog();
        initHeader();
        initContent();

        fetchBooksReactively();
    }

    private void initDialog() {
        createAuthorDialog = new Dialog();
        createAuthorDialog.setHeaderTitle("Создать автора");

        // dialog layout
        TextField firstNameField = new TextField("Имя");
        TextField lastNameField = new TextField("Фамилия");
        VerticalLayout dialogLayout = new VerticalLayout(firstNameField, lastNameField);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        createAuthorDialog.add(dialogLayout);

        // dialog footer
        Button saveButton = new Button("Создать", e -> {
            AuthorModel newAuthor = new AuthorModel();
            newAuthor.setFirstName(firstNameField.getValue());
            newAuthor.setLastName(lastNameField.getValue());
            authorService.createAuthor(newAuthor).subscribe(idModel -> {
                log.info("Book with Id: {} successfully saved", idModel.getId());
                getUI().ifPresent(ui -> ui.access(() -> {
                    newAuthor.setId(idModel.getId());
                    authors.add(newAuthor);
                    refreshGrid();
                }));
            });
            createAuthorDialog.close();
            firstNameField.clear();
            lastNameField.clear();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Отменить", e -> {
            createAuthorDialog.close();
            firstNameField.clear();
            lastNameField.clear();
        });
        createAuthorDialog.getFooter().add(cancelButton);
        createAuthorDialog.getFooter().add(saveButton);
    }

    private void initHeader() {
        Button addBookButton = new Button("Добавить автора", e -> createAuthorDialog.open());
        add(addBookButton);
    }

    private void initContent() {
        grid = new Grid<>(AuthorModel.class, false);
        grid.addColumn(new ComponentRenderer<>(RouterLink::new, (router, author) -> {
            router.setText(author.getId());
            router.setRoute(AuthorView.class, author.getId());
        })).setHeader("Id");
        grid.addColumn(AuthorModel::getFirstName).setHeader("Имя");
        grid.addColumn(AuthorModel::getLastName).setHeader("Фамиоия");
        grid.addColumn(AuthorModel::getCreatedDate).setHeader("Дата создания записи");
        grid.setEmptyStateText("Авторы не найдены");
        grid.setItems(authors);
        add(grid);
    }

    private void fetchBooksReactively() {
        authorService.getAuthors()
                .collectList()
                .subscribe(fetchedAuthors ->
                        getUI().ifPresent(ui -> ui.access(() -> {
                            this.authors = fetchedAuthors;
                            refreshGrid();
                        }))
                );
    }

    private void refreshGrid() {
        this.grid.setItems(authors);
    }
}
