package com.nurace11.bookapp.service;

import com.nurace11.bookapp.mapper.AuthorBookMapper;
import com.nurace11.bookapp.model.BookModel;
import com.nurace11.bookapp.model.IdModel;
import com.nurace11.bookapp.repository.AuthorRepository;
import com.nurace11.bookapp.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


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
        return bookRepository.save(bookMapper.toDocument(model)).map(bookDocument -> new IdModel(bookDocument.getId()));
    }
    public Mono<Void> deleteBookById(String id) {
        return bookRepository.deleteById(id);
    }

    //    public Mono<BookModel> addAuthors(String id, List<String> authorIds) {
//        return bookRepository.findById(id).flatMap(book ->
//                authorRepository.findAllById(authorIds)
//                        .collectList()
//                        .flatMap(authors -> {
//                            book.setAuthors(authors);
//                            return bookRepository.save(book);
//                        })
//                        .map(bookMapper::toModel)
//        );
//    }

//    public Mono<BookDocument> addAuthorsToBook(String bookId, List<String> authorIds) {
//        return bookRepository.findById(bookId)
//                .flatMap(book ->
//                        authorRepository.findAllById(authorIds)
//                                .collectList()
//                                .flatMap(authors -> {
//                                    book.setAuthors(authors);
//                                    authors.forEach(author -> author.getBooks().add(book));
//                                    return bookRepository.save(book)
//                                            .and(authorRepository.saveAll(authors))
//                                            .thenReturn(book);
//                                })
//                );
//    }

}
