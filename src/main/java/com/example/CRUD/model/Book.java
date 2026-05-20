package com.example.CRUD.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "Books")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Book {

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private Long id;

   @NotBlank(message = "Title is required")
   @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
   private String title;

   @NotBlank(message = "Author is required")
   @Size(min = 1, max = 100, message = "Author must be between 1 and 100 characters")
   private String author;

}
