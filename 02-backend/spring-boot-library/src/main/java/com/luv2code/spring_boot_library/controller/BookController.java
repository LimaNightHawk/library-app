package com.luv2code.spring_boot_library.controller;

import com.luv2code.spring_boot_library.entity.Book;
import com.luv2code.spring_boot_library.service.BookService;
import com.luv2code.spring_boot_library.utils.JwtParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService bookService;

    @Autowired
    public BookController(BookService bookService) {

        this.bookService = bookService;
    }

    @PutMapping("/secure/checkout")
    public Book checkoutBook(@RequestHeader("Authorization") String token, @RequestParam Long bookId) throws Exception {

        String userEmail = getUserEmailFromToken(token);
        return bookService.checkoutBook(userEmail, bookId);
    }

    @GetMapping(value = "/secure/isCheckedout")
    public Boolean isCheckedout(@RequestHeader("Authorization") String token, @RequestParam Long bookId) {

        String userEmail = getUserEmailFromToken(token);
        return bookService.isBookCheckedoutByUser(userEmail, bookId);
    }

    @GetMapping(value = "/secure/checkouts")
    public int getCheckoutCount(@RequestHeader("Authorization") String token) {

        System.out.println(token);
        String userEmail = getUserEmailFromToken(token);
        return bookService.getCheckoutsForUser(userEmail);
    }

    @DeleteMapping(value = "/secure/checkin")
    public Boolean getCheckouts(@RequestHeader("Authorization") String token, @RequestParam Long bookId) throws Exception {

        String userEmail = getUserEmailFromToken(token);
        bookService.checkinBook(userEmail, bookId);
        return true;
    }

    private String getUserEmailFromToken(String token) {

        JwtParser jwtParser = new JwtParser(token);
        return (String) jwtParser.getPayloadMap().get("sub");
    }
}
