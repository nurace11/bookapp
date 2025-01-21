package com.nurace11.bookapp.model;

import lombok.Data;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookModel {
    private String id;
    private String name;
    private LocalDate publishDate;
    private LocalDateTime createdDate;
    private List<String> authorIds;
}
