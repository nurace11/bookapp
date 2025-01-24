package com.nurace11.bookapp.ui.view.book;

import com.nurace11.bookapp.model.AuthorModel;
import com.nurace11.bookapp.model.BookModel;
import com.nurace11.bookapp.service.AuthorService;
import com.nurace11.bookapp.service.BookService;
import com.nurace11.bookapp.service.LibraryService;
import com.nurace11.bookapp.ui.MainLayout;
import com.nurace11.bookapp.ui.view.author.AuthorView;
import com.nurace11.bookapp.ui.view.author.AuthorsView;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Route(value = "books", layout = MainLayout.class)
public class BookView extends VerticalLayout implements HasUrlParameter<String> {

    private BookModel book;
    private final BookService bookService;
    private final AuthorService authorService;
    private final LibraryService libraryService;

    private Text headerText;
    private Grid<RowData> bookDetailsGrid;
    private Grid<AuthorModel> authorGrid;
    private Grid<AuthorModel> allAuthorsGrid;
    private Dialog addAuthorDialog;
    private Button saveAuthorButton;

    private List<AuthorModel> bookAuthors;
    private Set<String> setOfCheckedAuthors;

    public BookView(BookService bookService,
                    AuthorService authorService,
                    LibraryService libraryService) {

        this.bookService = bookService;
        this.authorService = authorService;
        this.libraryService = libraryService;

        bookAuthors = new ArrayList<>();

        initHeader();
        initBookDetailsSection();
        initAddAuthorDialog();
        initBookAuthorsSection();
    }

    private void initHeader() {
        headerText = new Text("Книга: ");
        add(headerText);
    }

    private void initBookDetailsSection() {
        bookDetailsGrid = new Grid<>();
        bookDetailsGrid.addColumn(RowData::getComponent).setHeader("Поле");
        bookDetailsGrid.addColumn(RowData::getDescription).setHeader("Значение");
        bookDetailsGrid.setWidthFull();
        add(bookDetailsGrid);
    }

    private void initBookAuthorsSection() {
        Button addAuthorButton = new Button("Добавить автора", e -> {
            addAuthorDialog.open();
            fetchAllAuthors();
        });
        HorizontalLayout horizontalLayout = new HorizontalLayout(JustifyContentMode.BETWEEN, new Text("Авторы книги"), addAuthorButton);
        horizontalLayout.setWidthFull();
        add(horizontalLayout);
        authorGrid = new Grid<>();
        authorGrid.addColumn(new ComponentRenderer<>(RouterLink::new, (router, author) -> {
            router.setText(author.getId());
            router.setRoute(AuthorView.class, author.getId());
        })).setHeader("ID");
        authorGrid.addColumn(AuthorModel::getFirstName).setHeader("Имя");
        authorGrid.addColumn(AuthorModel::getLastName).setHeader("Фамилия");
        authorGrid.setEmptyStateText("Авторы не найдены");
        add(authorGrid);
    }

    private void initAddAuthorDialog() {
        addAuthorDialog = new Dialog();
        addAuthorDialog.setWidth("65%");
        addAuthorDialog.setHeaderTitle("Добавить автора к книге");

        allAuthorsGrid = new Grid<>();

        setOfCheckedAuthors = new HashSet<>();
        allAuthorsGrid.addColumn(new ComponentRenderer<>(Checkbox::new, (checkbox, author) -> {
            checkbox.setValue(setOfCheckedAuthors.contains(author.getId()));
            checkbox.addValueChangeListener(event -> {
                if (event.getValue()) {
                    setOfCheckedAuthors.add(author.getId());
                } else {
                    setOfCheckedAuthors.remove(author.getId());
                }
            });
        })).setHeader("");
        allAuthorsGrid.addColumn(AuthorModel::getId).setHeader("ID");
        allAuthorsGrid.addColumn(AuthorModel::getFirstName).setHeader("Имя");
        allAuthorsGrid.addColumn(AuthorModel::getLastName).setHeader("Фамилия");
        allAuthorsGrid.setEmptyStateText("Авторы не найдены");
        addAuthorDialog.add(allAuthorsGrid);

        // dialog footer
        saveAuthorButton = new Button("Добавить");
        saveAuthorButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        addAuthorDialog.getFooter().add(new Button("Отмена", e -> addAuthorDialog.close()));
        addAuthorDialog.getFooter().add(saveAuthorButton);
    }

    private void fetchAllAuthors() {
        authorService.getAuthors()
                .collectList()
                .subscribe(allAuthors -> getUI().ifPresent(ui -> ui.access(() -> allAuthorsGrid.setItems(allAuthors))));
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String bookId) {
        bookService.getBook(bookId).subscribe(
                book -> {
                    if (Objects.nonNull(book)) {
                        this.book = book;
                        getUI().ifPresent(ui -> ui.access(() -> {
                            this.headerText.setText(headerText.getText() + book.getName());
                            this.bookDetailsGrid.setItems(List.of(
                                    new RowData("ID", book.getId()),
                                    new RowData("Название книги", book.getName()),
                                    new RowData("Дата выпуска", book.getPublishDate().toString()),
                                    new RowData("Дата создания записи", book.getCreatedDate().toString()))
                            );
                            for (String authorId : book.getAuthorIds()) {
                                this.authorService.getAuthor(authorId).subscribe(author -> {
                                    bookAuthors.add(author);
                                    getUI().ifPresent(ui2 -> ui2.access(this::refreshAuthorsGrid));
                                });
                            }
                            this.saveAuthorButton.addClickListener(e -> {
                                addAuthorDialog.close();
                                setOfCheckedAuthors.forEach(authorId -> {
                                            if (bookAuthors.stream().filter(bookAuthor -> bookAuthor.getId().equals(authorId)).findFirst().isEmpty()) {
                                                libraryService.addAuthorToBook(bookId, authorId).subscribe(author -> {
                                                    log.info("author {} successfully added to {} book", author.getId(), bookId);
                                                });
                                            } else {
                                                log.warn("Found duplicate {}", authorId);
                                            }
                                });
                            });
                        }));
                    } else {
                        this.headerText.setText("404 Книга не найдена");
                    }
                }
        );
    }

    private void loadBook(String bookId) {
        bookService.getBook(bookId).subscribe(b -> this.book = b);
    }

    private void refreshAuthorsGrid() {
        authorGrid.setItems(bookAuthors);
    }
}
