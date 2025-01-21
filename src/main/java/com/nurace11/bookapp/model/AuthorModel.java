package com.nurace11.bookapp.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AuthorModel {
    private String id;
    private String firstName;
    private String lastName;
    private LocalDateTime createdDate;
    private List<String> bookIds;
}
