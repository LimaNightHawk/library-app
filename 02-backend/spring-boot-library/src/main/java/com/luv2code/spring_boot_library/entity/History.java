package com.luv2code.spring_boot_library.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "history")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_email", length = 45)
    private String userEmail;

    @Column(name = "checkout_date", length = 45)
    private String checkoutDate;

    @Column(name = "returned_date", length = 45)
    private String returnedDate;

    @Column(name = "title", length = 45)
    private String title;

    @Column(name = "author", length = 45)
    private String author;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "img")
    private byte[] img;
}