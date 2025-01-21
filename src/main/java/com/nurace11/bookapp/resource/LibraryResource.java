package com.nurace11.bookapp.resource;

import com.nurace11.bookapp.document.AuthorDocument;
import com.nurace11.bookapp.document.BookDocument;
import com.nurace11.bookapp.model.ReportModel;
import com.nurace11.bookapp.service.LibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequestMapping("/library")
@RequiredArgsConstructor
public class LibraryResource {

    private final LibraryService libraryService;

    @GetMapping("/report")
    public Mono<ReportModel> getReport(@RequestParam LocalDate dateFrom, @RequestParam LocalDate dateTo) {
        return libraryService.getReport(dateFrom, dateTo);
    }

    @PostMapping("/add-book-to-author")
    public Mono<AuthorDocument> addBookToAuthor(@RequestParam String authorId, @PathVariable String bookId) {
        return libraryService.addBookToAuthor(authorId, bookId);
    }

    @PostMapping("/add-author-to-book")
    public Mono<BookDocument> addAuthorToBook(@RequestParam String bookId, @RequestParam String authorId) {
        return libraryService.addAuthorToBook(bookId, authorId);
    }
}
