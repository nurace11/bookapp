package com.nurace11.bookapp.repository;

import com.nurace11.bookapp.document.BookDocument;
import com.nurace11.bookapp.model.BookModel;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface BookRepository extends ReactiveMongoRepository<BookDocument, String> {
    Flux<BookDocument> findByName(String name);

    @Query("{ 'publishDate': { $gte: ?0, $lte: ?1 } }")
    Flux<BookDocument> findBooksInPublishDateRange(LocalDate startDate, LocalDate endDate);
}
