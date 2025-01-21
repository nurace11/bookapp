package com.nurace11.bookapp.mapper;

import com.nurace11.bookapp.document.AuthorDocument;
import com.nurace11.bookapp.document.BookDocument;
import com.nurace11.bookapp.model.AuthorModel;
import com.nurace11.bookapp.model.BookModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorBookMapper {
    AuthorModel toModel(AuthorDocument authorDocument);

    AuthorDocument toDocument(AuthorModel authorModel);

    BookModel toModel(BookDocument bookDocument);

    BookDocument toDocument(BookModel bookModel);
}
