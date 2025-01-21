package com.nurace11.bookapp.repository;

import com.nurace11.bookapp.document.AuthorDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface AuthorRepository extends ReactiveMongoRepository<AuthorDocument, String> {
}
