package com.nurace11.bookapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportBookModel {
    private String bookId;
    private String bookName;
    private LocalDate publishDate;
    private LocalDateTime createdDate;
//    private List<AuthorModel> authors;
}
