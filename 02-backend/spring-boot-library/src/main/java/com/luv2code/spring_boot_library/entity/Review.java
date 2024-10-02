package com.luv2code.spring_boot_library.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_email", length = 45)
    private String userEmail;

    @Column(name = "date")
    private Instant date;

    @Column(name = "rating", precision = 3, scale = 2)
    private double rating;

    @Column(name = "book_id")
    private Long bookId;

    @Lob
    @Column(name = "review_description")
    private String reviewDescription;
}