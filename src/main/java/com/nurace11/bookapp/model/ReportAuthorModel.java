package com.nurace11.bookapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportAuthorModel {
    private String authorId;
    private String firstName;
    private String lastName;
    private LocalDateTime createdDate;
}
