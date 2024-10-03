package com.luv2code.spring_boot_library.service;

import com.luv2code.spring_boot_library.dao.BookRepository;
import com.luv2code.spring_boot_library.dao.CheckoutRepository;
import com.luv2code.spring_boot_library.entity.Book;
import com.luv2code.spring_boot_library.entity.Checkout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookService {

    private BookRepository bookRepository;
    private CheckoutRepository checkoutRepository;

    @Autowired
    public BookService(BookRepository bookRepository, CheckoutRepository checkoutRepository) {

        this.bookRepository = bookRepository;
        this.checkoutRepository = checkoutRepository;
    }

    public Book checkoutBook(String userEmail, Long bookId) throws Exception {

        Optional<Book> optionalBook = bookRepository.findById(bookId);
        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        if (!optionalBook.isPresent() || validateCheckout != null || optionalBook.get().getCopiesAvailable() <= 0) {
            throw new Exception("Book not available or already checked out");
        }

        Book book = optionalBook.get();
        book.setCopiesAvailable(book.getCopiesAvailable() - 1);
        bookRepository.save(book);

        Checkout checkout = new Checkout(
                userEmail,
                LocalDate.now().toString(),
                LocalDate.now().plusDays(7).toString(),
                bookId
        );

        checkoutRepository.save(checkout);

        return book;
    }

    public Boolean isBookCheckedoutByUser(String userEmail, Long bookId) {

        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);

        return validateCheckout != null;
    }

    public int getCheckoutsForUser(String userEmail) {

        return checkoutRepository.findByUserEmail(userEmail).size();
    }

    public void checkinBook(String userEmail, Long bookId) throws Exception {

        Optional<Book> optionalBook = bookRepository.findById(bookId);
        Checkout checkout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        if (!optionalBook.isPresent() || checkout == null ) {
            throw new Exception("Book not available or not checked out");
        }

        Book book = optionalBook.get();
        book.setCopiesAvailable(book.getCopiesAvailable() + 1);
        bookRepository.save(book);

        checkoutRepository.delete(checkout);

    }

}
