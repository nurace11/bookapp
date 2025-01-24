package com.nurace11.bookapp.service;

import com.nurace11.bookapp.model.AuthorModel;
import com.nurace11.bookapp.repository.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class AuthorServiceTest {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void saveAuthor() {
        AuthorModel createAuthor = new AuthorModel();
        createAuthor.setFirstName("FirstName");
        createAuthor.setLastName("LastName");

        authorService.createAuthor(createAuthor).subscribe(savedIdModel -> {

            assertThat(savedIdModel.getId()).isNotNull();

            authorRepository.findById(savedIdModel.getId()).subscribe(fetchedIdModel -> {
                assertThat(fetchedIdModel).isNotNull();
                assertThat(fetchedIdModel.getId()).isNotNull();
                assertThat(fetchedIdModel.getCreatedDate()).isNotNull();
                assertThat(fetchedIdModel.getFirstName()).isEqualTo(createAuthor.getFirstName());
                assertThat(fetchedIdModel.getLastName()).isEqualTo(createAuthor.getLastName());
            });
        });
    }
}