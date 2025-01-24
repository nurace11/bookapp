package com.nurace11.bookapp.service;

import com.nurace11.bookapp.document.AuthorDocument;
import com.nurace11.bookapp.mapper.AuthorBookMapper;
import com.nurace11.bookapp.model.AuthorModel;
import com.nurace11.bookapp.model.IdModel;
import com.nurace11.bookapp.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorBookMapper authorMapper;

    public Flux<AuthorModel> getAuthors() {
        return authorRepository.findAll().map(authorMapper::toModel);
    }

    public Mono<AuthorModel> getAuthor(String id) {
        return authorRepository.findById(id).map(authorMapper::toModel);
    }

    public Mono<IdModel> createAuthor(AuthorModel model) {
        AuthorDocument document = authorMapper.toDocument(model);
        document.setCreatedDate(LocalDateTime.now());
        return authorRepository.save(document)
                .map(author -> new IdModel(author.getId()));
    }

    public Mono<Void> deleteAuthor(String id) {
        return authorRepository.deleteById(id);
    }
}
