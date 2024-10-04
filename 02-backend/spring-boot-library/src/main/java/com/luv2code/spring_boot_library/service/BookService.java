package com.luv2code.spring_boot_library.service;

import com.luv2code.spring_boot_library.dao.BookRepository;
import com.luv2code.spring_boot_library.dao.CheckoutRepository;
import com.luv2code.spring_boot_library.entity.Book;
import com.luv2code.spring_boot_library.entity.Checkout;
import com.luv2code.spring_boot_library.responsemodels.ShelfCurrentLoansResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
                LocalDate.now().plusDays(5).toString(),
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
        if (!optionalBook.isPresent() || checkout == null) {
            throw new Exception("Book not available or not checked out");
        }

        Book book = optionalBook.get();
        book.setCopiesAvailable(book.getCopiesAvailable() + 1);
        bookRepository.save(book);

        checkoutRepository.delete(checkout);
    }

    public List<ShelfCurrentLoansResponse> currentLoans(String userEmail) throws ParseException {

        List<ShelfCurrentLoansResponse> response = new ArrayList<>();
        List<Checkout> checkouts = checkoutRepository.findByUserEmail(userEmail);
        List<Long> bookIdList = checkouts.stream().map(c -> c.getBookId()).toList();
        List<Book> books = bookRepository.findAllById(bookIdList);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Book book : books) {
            Optional<Checkout> checkout = checkouts.stream()
                    .filter(x -> x.getBookId() == book.getId()).findFirst();
            if (checkout.isPresent()) {
                Date d1 = sdf.parse(checkout.get().getReturnDate());
                Date d2 = sdf.parse(LocalDate.now().toString());
                TimeUnit time = TimeUnit.DAYS;
                long diff = time.convert(d1.getTime() - d2.getTime(), TimeUnit.MILLISECONDS);
                response.add(new ShelfCurrentLoansResponse(
                        book,
                        (int) diff
                ));
            }
        }
        return response;
    }

    public void returnBook(String userEmail, Long bookId) throws Exception {

        Optional<Book> bookOption = bookRepository.findById(bookId);
        Checkout validate = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        if (!bookOption.isPresent() || validate == null) {
            throw new Exception("Book does not exist or not checked out by user");
        }
        Book book = bookOption.get();
        book.setCopiesAvailable(book.getCopiesAvailable() + 1);
        bookRepository.save(book);
        checkoutRepository.deleteById(validate.getId());
    }

    public void renewLoan(String userEmail, Long bookId) throws Exception {

        Optional<Book> bookOption = bookRepository.findById(bookId);
        Checkout checkout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        if (!bookOption.isPresent() || checkout == null) {
            throw new Exception("Book does not exist or not checked out by user");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date d1 = sdf.parse(checkout.getCheckoutDate());
        Date d2 = sdf.parse(LocalDate.now().toString());
        if (!d1.after(d2)) {
            checkout.setReturnDate(LocalDate.now().plusDays(7).toString());
            checkoutRepository.save(checkout);
        }
    }
}
