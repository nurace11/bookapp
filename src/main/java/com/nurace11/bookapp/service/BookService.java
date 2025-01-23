package com.nurace11.bookapp.service;

import com.nurace11.bookapp.document.BookDocument;
import com.nurace11.bookapp.mapper.AuthorBookMapper;
import com.nurace11.bookapp.model.BookModel;
import com.nurace11.bookapp.model.IdModel;
import com.nurace11.bookapp.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorBookMapper bookMapper;

    public Mono<BookModel> getBook(String id) {
        return bookRepository.findById(id).map(bookMapper::toModel);
    }

    public Flux<BookModel> getBooks() {
        return bookRepository.findAll().map(bookMapper::toModel);
    }

    public Mono<IdModel> createBook(BookModel model) {
        BookDocument document = bookMapper.toDocument(model);
        document.setCreatedDate(LocalDateTime.now());
        return bookRepository.save(document)
                .map(bookDocument -> new IdModel(bookDocument.getId()));
    }

    public Mono<Void> deleteBookById(String id) {
        return bookRepository.deleteById(id);
    }
}
