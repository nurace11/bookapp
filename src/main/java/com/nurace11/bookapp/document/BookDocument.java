package com.nurace11.bookapp.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document
public class BookDocument {
    @Id
    private String id;
    private String name;
    private LocalDate publishDate;
    private LocalDateTime createdDate;
    private List<String> authorIds = new ArrayList<>();
}
