package com.nurace11.bookapp.ui.view.book;

import com.nurace11.bookapp.model.BookModel;
import com.nurace11.bookapp.service.BookService;
import com.nurace11.bookapp.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Slf4j
@Route(value = "books", layout = MainLayout.class)
@PageTitle("BooksView")
public class BooksView extends VerticalLayout {

    private final BookService bookService;

    private List<BookModel> books = new ArrayList<>();
    private Grid<BookModel> grid;
    private BookModel selectedBook;

    private Dialog createBookDialog;

    public BooksView(BookService bookService) {
        this.bookService = bookService;

        initDialog();
        initHeader();
        initContent();

        fetchBooksReactively();
    }

    private void initHeader() {
        Button addBookButton = new Button("Добавить книгу", e -> createBookDialog.open());
        HorizontalLayout horizontalLayout = new HorizontalLayout(addBookButton);
        add(horizontalLayout);
    }

    private void initDialog() {
        createBookDialog = new Dialog();
        createBookDialog.setHeaderTitle("Создать книгу");

        // dialog layout
        TextField bookNameField = new TextField("Наименование");
        DatePicker bookPublicDatedatePicker =new DatePicker("Дата публикации", LocalDate.now());
        VerticalLayout dialogLayout = new VerticalLayout(bookNameField, bookPublicDatedatePicker);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        createBookDialog.add(dialogLayout);

        // dialog footer
        Button saveButton = new Button("Создать", e -> {
            BookModel newBook = new BookModel();
            newBook.setName(bookNameField.getValue());
            newBook.setPublishDate(bookPublicDatedatePicker.getValue());
            bookService.createBook(newBook).subscribe(idModel -> {
                log.info("Book with Id: {} successfully saved", idModel.getId());
                getUI().ifPresent(ui -> ui.access(() -> {
                    newBook.setId(idModel.getId());
                    books.add(newBook);
                    refreshGrid();
                }));
            });
            createBookDialog.close();
            bookNameField.clear();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Отменить", e -> {
            createBookDialog.close();
            bookNameField.clear();
        });
        createBookDialog.getFooter().add(cancelButton);
        createBookDialog.getFooter().add(saveButton);
    }

    private void initContent() {
        grid = new Grid<>(BookModel.class, false);
        grid.addColumn(new ComponentRenderer<>(RouterLink::new, (router, book) -> {
            router.setText(book.getId());
            router.setRoute(BookView.class, book.getId());
        })).setHeader("Id");
        grid.addColumn(BookModel::getName).setHeader("Название книги");
        grid.addColumn(BookModel::getPublishDate).setHeader("Дата публикации");
        grid.addColumn(BookModel::getCreatedDate).setHeader("Дата создания записи");
        grid.addColumn(new ComponentRenderer<>(Button::new, (button, book) -> {
            button.addThemeVariants(ButtonVariant.LUMO_ICON,
                    ButtonVariant.LUMO_ERROR,
                    ButtonVariant.LUMO_TERTIARY);
            button.addClickListener(e -> {
                this.bookService.deleteBookById(book.getId()).subscribe(v -> {});
                removeBook(book);
            });
            button.setIcon(new Icon(VaadinIcon.TRASH));
        }));
        grid.setEmptyStateText("Книги не найдены");

        grid.setItems(books);

        add(grid);
    }

    private void removeBook(BookModel book) {
        log.info("Book {} successfully deleted", book.getId());
        if (books.contains(book)) {
            this.books.remove(book);
            this.refreshGrid();
        }
    }

    private void updateGridContentLazy() {
        grid.setItems(query ->
                bookService.getBooks().skip(query.getOffset())
                        .take(query.getLimit())
                        .toStream()
        );
    }

    private void fetchBooksReactively() {
        bookService.getBooks()
                .collectList()
                .subscribe(fetchedBooks ->
                        getUI().ifPresent(ui -> ui.access(() -> {
                            this.books = fetchedBooks;
                            refreshGrid();
                        }))
                );
    }


    private void refreshGrid() {
        grid.setItems(books);
    }
}
