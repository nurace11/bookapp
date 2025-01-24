package com.nurace11.bookapp.service;

import com.nurace11.bookapp.document.AuthorDocument;
import com.nurace11.bookapp.document.BookDocument;
import com.nurace11.bookapp.model.ReportAuthorModel;
import com.nurace11.bookapp.model.ReportBookModel;
import com.nurace11.bookapp.model.ReportModel;
import com.nurace11.bookapp.repository.AuthorRepository;
import com.nurace11.bookapp.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public Mono<ReportModel> getReport(LocalDate dateFrom, LocalDate dateTo) {
        return bookRepository.findAll()
                .filter(book -> book.getPublishDate() != null
                        && book.getPublishDate().isAfter(dateFrom)
                        && book.getPublishDate().isBefore(dateTo))
                .collectList()
                .flatMap(books -> {
                    List<ReportBookModel> reportBooks = books.stream()
                            .map(book -> new ReportBookModel(
                                    book.getId(),
                                    book.getName(),
                                    book.getPublishDate(),
                                    book.getCreatedDate())
                            ).toList();

                    Set<String> authorIds = books.stream()
                            .flatMap(book -> book.getAuthorIds().stream())
                            .collect(Collectors.toSet());

                    return authorRepository.findAllById(authorIds)
                            .collectList()
                            .map(authors -> {
                                List<ReportAuthorModel> reportAuthors = authors.stream()
                                        .map(author -> new ReportAuthorModel(
                                                author.getFirstName(),
                                                author.getLastName(),
                                                author.getCreatedDate())
                                        ).toList();

                                return new ReportModel(reportBooks, reportAuthors);
                            });
                });
    }

    public Mono<AuthorDocument> addBookToAuthor(String authorId, String bookId) {
        return authorRepository.findById(authorId)
                .flatMap(author -> bookRepository.findById(bookId)
                        .flatMap(book -> {
                            author.getBookIds().add(bookId);
                            book.getAuthorIds().add(authorId);
                            return Mono.zip(authorRepository.save(author), bookRepository.save(book));
                        }))
                .map(tuple -> tuple.getT1());
    }

//    public Mono<BookDocument> addAuthorToBook(String bookId, String authorId) {
//        System.out.println("addAuthorToBook book: %s author: %s".formatted(bookId, authorId));
//
//        return bookRepository.findById(bookId)
//                .flatMap(book -> authorRepository.findById(authorId)
//                        .flatMap(author -> {
//                            book.getAuthorIds().add(authorId);
//                            author.getBookIds().add(bookId);
//                            return Mono.zip(bookRepository.save(book), authorRepository.save(author));
//                        }))
//                .map(tuple -> tuple.getT1());
//    }

    public Mono<BookDocument> addAuthorToBook(String bookId, String authorId) {
//        System.out.println("addAuthorToBook book: %s author: %s".formatted(bookId, authorId));

        return bookRepository.findById(bookId)
                .flatMap(book -> authorRepository.findById(authorId)
                        .flatMap(author -> {
                            book.getAuthorIds().add(authorId);
                            author.getBookIds().add(bookId);
                            return Mono.zip(bookRepository.save(book), authorRepository.save(author));
                        }))
                .map(tuple -> tuple.getT1());
    }
}
