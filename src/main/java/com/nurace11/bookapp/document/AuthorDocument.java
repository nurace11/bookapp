package com.nurace11.bookapp.document;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document
public class AuthorDocument {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private LocalDateTime createdDate;
    private List<String> bookIds = new ArrayList<>();
}
