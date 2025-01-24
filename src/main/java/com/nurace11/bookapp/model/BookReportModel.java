package com.nurace11.bookapp.model;

import lombok.Data;

import java.util.List;

@Data
public class BookReportModel {
    private BookModel bookModel;
    private List<AuthorModel> authors;
}
