package com.nurace11.bookapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportModel {
    private List<ReportBookModel> books;
    private List<ReportAuthorModel> authors;
}
