package com.nurace11.bookapp.repository;

import com.nurace11.bookapp.document.BookDocument;
import com.nurace11.bookapp.model.BookModel;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface BookRepository extends ReactiveMongoRepository<BookDocument, String> {
    Flux<BookModel> findByName(String name);
}
