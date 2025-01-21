package com.nurace11.bookapp.resource;

import com.nurace11.bookapp.model.AuthorModel;
import com.nurace11.bookapp.model.IdModel;
import com.nurace11.bookapp.service.AuthorService;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/authors")
public class AuthorResource {

    private final AuthorService authorService;

    @GetMapping("/{authorId}")
    public Mono<AuthorModel> getAuthor(@PathVariable String authorId) {
        return authorService.getAuthor(authorId);
    }

    @GetMapping
    public Flux<AuthorModel> getAuthors() {
        return authorService.getAuthors();
    }

    @PostMapping
    public Mono<IdModel> createAuthor(@RequestBody AuthorModel model) {
        return authorService.createAuthor(model);
    }
}
