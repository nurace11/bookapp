package com.nurace11.bookapp.ui.view.report;

import com.nurace11.bookapp.docs.ReportGenerator;
import com.nurace11.bookapp.model.BookModel;
import com.nurace11.bookapp.service.BookService;
import com.nurace11.bookapp.service.LibraryService;
import com.nurace11.bookapp.ui.MainLayout;
import com.nurace11.bookapp.ui.view.book.BookView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.StreamResource;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.Objects;

@Route(value = "report", layout = MainLayout.class)
public class ReportView extends VerticalLayout {

    private ReportGenerator reportGenerator;
    private LibraryService libraryService;
    private BookService bookService;
    private LocalDate dateFrom;
    private LocalDate dateTo;


    private Grid<BookModel> booksGrid;
    private HorizontalLayout reportButtonsLayout;
    private Button downloadReportButton;
    private Button seeReportButton;

    private Anchor downloadAnchor;
    private Anchor pdfLinkAnchor;

    public ReportView(BookService bookService,
                      LibraryService libraryService,
                      ReportGenerator reportGenerator) {

        this.bookService = bookService;
        this.libraryService = libraryService;
        this.reportGenerator = reportGenerator;

        initComponents();
    }

    private void initComponents() {
        initHeader();
        initBooksSection();
    }

    private void initHeader() {
        HorizontalLayout headerLayout = new HorizontalLayout(JustifyContentMode.CENTER);
        headerLayout.setWidthFull();
        headerLayout.add(new H3("Создать отчет"));
        add(headerLayout);

        VerticalLayout headerDatePickedLayout = new VerticalLayout(Alignment.CENTER);
        headerDatePickedLayout.add(new DatePicker("Дата публикации с", e -> this.dateFrom = e.getValue()));
        headerDatePickedLayout.add(new DatePicker("Дата публикации по", e -> this.dateTo = e.getValue()));
        Button button = new Button("Найти книги", e -> {
            loadBooksByDate();
            generateReport();
        });
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        headerDatePickedLayout.add(button);
        add(headerDatePickedLayout);
    }

    private void initBooksSection() {
        booksGrid = new Grid<>();
        booksGrid.addColumn(new ComponentRenderer<>(RouterLink::new, (router, book) -> {
            router.setText(book.getId());
            router.setRoute(BookView.class, book.getId());
        })).setHeader("Id");
        booksGrid.addColumn(BookModel::getName).setHeader("Название книги");
        booksGrid.addColumn(BookModel::getPublishDate).setHeader("Дата публикации");
        booksGrid.addColumn(BookModel::getCreatedDate).setHeader("Дата создания записи");
        booksGrid.setEmptyStateText("Книги не найдены");
        booksGrid.setVisible(false);
        add(booksGrid);

        downloadAnchor = new Anchor();
        downloadAnchor.getElement().getStyle().set("display", "none");
        downloadAnchor.getElement().setAttribute("download", true);
        add(downloadAnchor);

        pdfLinkAnchor = new Anchor();
        pdfLinkAnchor.getElement().getStyle().set("display", "none");
        pdfLinkAnchor.getElement().setAttribute("target", "_blank");
        add(pdfLinkAnchor);

        reportButtonsLayout = new HorizontalLayout();
        seeReportButton = new Button("Посмотреть отчет", VaadinIcon.EYE.create(), e -> pdfLinkAnchor.getElement().callJsFunction("click"));
        reportButtonsLayout.add(seeReportButton);
        downloadReportButton = new Button("Скачать отчет", VaadinIcon.DOWNLOAD.create(), e -> downloadAnchor.getElement().callJsFunction("click"));
        reportButtonsLayout.add(downloadReportButton);
        reportButtonsLayout.setVisible(false);
        add(reportButtonsLayout);
    }

    private void generateReport() {
        if (Objects.nonNull(dateFrom) && Objects.nonNull(dateTo)) {
            libraryService.getReport(dateFrom, dateTo).subscribe(reportModel -> {
                StreamResource streamResource = new StreamResource("book-app-report-%s.pdf".formatted(LocalDate.now()), () -> new ByteArrayInputStream(reportGenerator.generateReport(reportModel, dateFrom, dateTo)));
                getUI().ifPresent(ui -> ui.access(() -> {
                    this.downloadAnchor.setHref(streamResource);
                    this.pdfLinkAnchor.setHref(streamResource);
                }));
            });
        }
    }

    private void loadBooksByDate() {
        if (Objects.nonNull(dateFrom) || Objects.nonNull(dateTo)) {
            this.bookService.getBooksInRange(dateFrom, dateTo).collectList().subscribe(books -> getUI().ifPresent(ui -> ui.access(() -> {
                if (!CollectionUtils.isEmpty(books)) {
                    booksGrid.setItems(books);
                    booksGrid.setVisible(true);
                    reportButtonsLayout.setVisible(true);
                } else {
                    booksGrid.setVisible(false);
                    reportButtonsLayout.setVisible(false);
                }
            })));
        }
    }
}
