package com.luv2code.spring_boot_library.controller;

import com.luv2code.spring_boot_library.entity.Book;
import com.luv2code.spring_boot_library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/books")
public class BookController {

    public static final String TEST_MAIL = "testuser@email.com";
    private BookService bookService;

    @Autowired
    public BookController(BookService bookService) {

        this.bookService = bookService;
    }

    @PutMapping("/secure/checkout")
    public Book checkoutBook(@RequestParam Long bookId) throws Exception {

        return bookService.checkoutBook(TEST_MAIL, bookId);
    }

    @GetMapping(value = "/secure/isCheckedout")
    public Boolean isCheckedout(@RequestParam Long bookId) {

        return bookService.isBookCheckedoutByUser(TEST_MAIL, bookId);
    }

    @GetMapping(value = "/secure/checkouts")
    public int getCheckoutCount() {

        return bookService.getCheckoutsForUser(TEST_MAIL);
    }

    @DeleteMapping(value = "/secure/checkin")
    public Boolean getCheckouts(@RequestParam Long bookId) throws Exception {

        bookService.checkinBook(TEST_MAIL, bookId);
        return true;
    }
}
