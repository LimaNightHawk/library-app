package com.luv2code.spring_boot_library.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_email", length = 45)
    private String userEmail;

    @Column(name = "title", length = 45)
    private String title;

    @Lob
    @Column(name = "question")
    private String question;

    @Column(name = "admin_email", length = 45)
    private String adminEmail;

    @Lob
    @Column(name = "response")
    private String response;

    @Column(name = "closed")
    private Boolean closed;
}