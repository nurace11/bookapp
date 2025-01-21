package com.nurace11.bookapp.resource;

import com.nurace11.bookapp.model.BookModel;
import com.nurace11.bookapp.model.IdModel;
import com.nurace11.bookapp.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookResource {

    private final BookService bookService;

    @GetMapping("/{bookId}")
    public Mono<BookModel> getBook(@PathVariable String bookId) {
        return bookService.getBook(bookId);
    }

    @GetMapping
    public Flux<BookModel> getBooks() {
        return bookService.getBooks();
    }

    @PostMapping
    public Mono<IdModel> createBook(@RequestBody BookModel bookModel) {
        return bookService.createBook(bookModel);
    }

    @DeleteMapping("/{bookId}")
    public Mono<Void> deleteById(@PathVariable String bookId) {
        return bookService.deleteBookById(bookId);
    }
}
