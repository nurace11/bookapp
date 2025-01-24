package com.nurace11.bookapp.service;

import com.nurace11.bookapp.document.AuthorDocument;
import com.nurace11.bookapp.document.BookDocument;
import com.nurace11.bookapp.model.AuthorModel;
import com.nurace11.bookapp.model.BookModel;
import com.nurace11.bookapp.repository.AuthorRepository;
import com.nurace11.bookapp.repository.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LibraryServiceTest {

    @Autowired
    private LibraryService libraryService;

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @AfterEach
    void cleanDatabase() {
        mongoTemplate.executeCommand("{dropDatabase: 1}").subscribe(System.out::println);
    }

    @Test
    void addAuthorToBookTest() {
        bookRepository.save(new BookDocument()).subscribe(book ->
                authorRepository.save(new AuthorDocument()).subscribe(author ->
                        libraryService.addAuthorToBook(book.getId(), author.getId()).subscribe(bookWithAuthors -> {
                            assertThat(bookWithAuthors.getAuthorIds()).isNotEmpty();
                            assertThat(bookWithAuthors.getAuthorIds()).containsOnly(author.getId());
                        })
                )
        );
    }

    @Test
    void createReportTest() {
        bookRepository.save(createDocument(LocalDate.of(2000, 10, 10))).subscribe(book ->
                authorRepository.save(new AuthorDocument()).subscribe(author ->
                        libraryService.addAuthorToBook(book.getId(), author.getId()).subscribe(bookWithAuthors ->
                                libraryService.getReportV2(LocalDate.of(1999, 10, 10), LocalDate.of(2001, 10, 10)).subscribe(report -> {
                                    BookModel reportBook = report.getBookModel();
                                    assertThat(reportBook.getId()).isEqualTo(book.getId());
                                    assertThat(reportBook.getPublishDate()).isEqualTo(book.getPublishDate());
                                    assertThat(report.getAuthors()).extracting(AuthorModel::getId).containsOnly(author.getId());
                                })
                        )
                )
        );
    }

    private BookDocument createDocument(LocalDate date) {
        BookDocument bookDocument = new BookDocument();
        bookDocument.setPublishDate(date);
        return bookDocument;
    }
}