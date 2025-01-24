package com.nurace11.bookapp.ui.view.author;

import com.nurace11.bookapp.model.AuthorModel;
import com.nurace11.bookapp.model.BookModel;
import com.nurace11.bookapp.service.AuthorService;
import com.nurace11.bookapp.service.BookService;
import com.nurace11.bookapp.service.LibraryService;
import com.nurace11.bookapp.ui.MainLayout;
import com.nurace11.bookapp.ui.view.book.BookView;
import com.nurace11.bookapp.ui.view.util.RowData;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Route(value = "authors", layout = MainLayout.class)
public class AuthorView extends VerticalLayout implements HasUrlParameter<String> {

    private AuthorService authorService;
    private BookService bookService;
    private LibraryService libraryService;

    private AuthorModel author;
    private List<RowData> authorDetails = new LinkedList<>();
    private Grid<RowData> authorDetailsGrid;
    private Grid<BookModel> booksGrid;
    private List<BookModel> authorBooks = new LinkedList<>();
    private Grid<BookModel> allBooksGrid;
    private Set<String> selectedBookIds = new HashSet<>();

    public AuthorView(AuthorService authorService, BookService bookService, LibraryService libraryService) {
        this.authorService = authorService;
        this.bookService = bookService;
        this.libraryService = libraryService;

        initComponents();
    }

    private void initComponents() {
        // header
        new Text("");

        // detailsGrid
        authorDetailsGrid = new Grid<>();
        authorDetailsGrid.addColumn(RowData::getComponent).setHeader("Поле");
        authorDetailsGrid.addColumn(RowData::getDescription).setHeader("Значение");
        authorDetailsGrid.setItems(authorDetails);
        add(authorDetailsGrid);

        // === BooksSection === \\

        // addBookDialog
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Добавить книги");
        dialog.setWidth("65%");

        allBooksGrid = new Grid<>();
        allBooksGrid.addColumn(new ComponentRenderer<>(Checkbox::new, (checkbox, book) -> {
            checkbox.addValueChangeListener(e -> {
                if (Boolean.TRUE.equals(e.getValue())) {
                    selectedBookIds.add(book.getId());
                } else {
                    selectedBookIds.remove(book.getId());
                }
            });
        })).setHeader("");
        allBooksGrid.addColumn(new ComponentRenderer<>(RouterLink::new, (router, book) -> {
            router.setText(book.getId());
            router.setRoute(BookView.class, book.getId());
        })).setHeader("Id");
        allBooksGrid.addColumn(BookModel::getName).setHeader("Название книги");
        allBooksGrid.addColumn(BookModel::getPublishDate).setHeader("Дата публикации");
        allBooksGrid.addColumn(BookModel::getCreatedDate).setHeader("Дата создания записи");
        allBooksGrid.setEmptyStateText("Книги не найдены");
        dialog.add(allBooksGrid);

        dialog.getFooter().add(new Button("Отмена", e -> dialog.close()));
        Button saveBooksButton = new Button("Добавить", e -> {
            dialog.close();
            addBooks();
        });
        saveBooksButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        dialog.getFooter().add(saveBooksButton);

        //
        Button addBookButton = new Button("Добавить книги", e -> {
            dialog.open();
            updateAllBooksGrid();
        });
        HorizontalLayout horizontalLayout = new HorizontalLayout(JustifyContentMode.BETWEEN, new Text("Книги автора"), addBookButton);
        horizontalLayout.setWidthFull();
        add(horizontalLayout);

        // booksGrid
        booksGrid = new Grid<>();
        booksGrid.addColumn(new ComponentRenderer<>(RouterLink::new, (router, book) -> {
            router.setText(book.getId());
            router.setRoute(BookView.class, book.getId());
        })).setHeader("Id");
        booksGrid.addColumn(BookModel::getName).setHeader("Название книги");
        booksGrid.addColumn(BookModel::getPublishDate).setHeader("Дата публикации");
        booksGrid.addColumn(BookModel::getCreatedDate).setHeader("Дата создания записи");
        booksGrid.setEmptyStateText("Книги не найдены");
        add(booksGrid);
    }

    private void addBooks() {
        for (String bookId : selectedBookIds) {
            libraryService.addBookToAuthor(author.getId(), bookId).subscribe(v -> {
                loadAuthor(author.getId());
            });
        }
    }

    private void updateAllBooksGrid() {
        bookService.getBooks().collectList().subscribe(booksList -> getUI().ifPresent(ui -> ui.access(() -> allBooksGrid.setItems(booksList))));
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String authorId) {
        this.loadAuthor(authorId);
    }

    private void loadAuthor(String id) {
        this.authorService.getAuthor(id).subscribe((authorModel -> {
            this.author = authorModel;
            this.updateAuthorDetails();
            this.updateBooksGrid();
        }));
    }

    private void updateAuthorDetails() {
        this.authorDetails = (List.of(
                new RowData("ID", author.getId()),
                new RowData("Фамилия", author.getLastName()),
                new RowData("Имя", author.getFirstName()),
                new RowData("Дата создания записи", author.getCreatedDate() == null ? "" : author.getCreatedDate().toString())
        ));
        getUI().ifPresent(ui -> ui.access(() -> this.authorDetailsGrid.setItems(authorDetails)));
    }

    private void updateBooksGrid() {
        if (Objects.nonNull(author) && !CollectionUtils.isEmpty(author.getBookIds())) {
            author.getBookIds().forEach(bookId ->
                    bookService.getBook(bookId).subscribe(bookModel -> {
                        authorBooks.add(bookModel);
                        getUI().ifPresent(ui -> ui.access(() -> this.booksGrid.setItems(authorBooks)));
                    })
            );
        }
    }
}
