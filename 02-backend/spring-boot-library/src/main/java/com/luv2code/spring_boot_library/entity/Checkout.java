package com.luv2code.spring_boot_library.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "checkout")
public class Checkout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_email", length = 45)
    private String userEmail;

    @Column(name = "checkout_date", length = 45)
    private String checkoutDate;

    @Column(name = "return_date", length = 45)
    private String returnDate;

    @Column(name = "book_id")
    private Long bookId;
}