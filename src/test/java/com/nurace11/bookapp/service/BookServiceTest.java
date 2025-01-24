package com.nurace11.bookapp.service;

import com.nurace11.bookapp.model.BookModel;
import com.nurace11.bookapp.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void saveBook() {
        BookModel createBook = new BookModel();
        createBook.setName("bookName");
        createBook.setPublishDate(LocalDate.now());

        bookService.createBook(createBook).subscribe(savedIdModel -> {
            System.out.println("savedIdModel = " + savedIdModel);
            assertThat(savedIdModel.getId()).isNotNull();

            bookRepository.findById(savedIdModel.getId()).subscribe(foundBook -> {
                assertThat(foundBook).isNotNull();
                assertThat(foundBook.getId()).isNotNull();
                assertThat(foundBook.getCreatedDate()).isNotNull();
                assertThat(foundBook.getName()).isEqualTo(createBook.getName());
                assertThat(foundBook.getPublishDate()).isEqualTo(createBook.getPublishDate());
            });
        });
    }

}