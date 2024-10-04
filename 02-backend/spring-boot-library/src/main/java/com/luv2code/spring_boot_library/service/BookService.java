package com.luv2code.spring_boot_library.service;

import com.luv2code.spring_boot_library.dao.BookRepository;
import com.luv2code.spring_boot_library.dao.CheckoutRepository;
import com.luv2code.spring_boot_library.dao.HistoryRepository;
import com.luv2code.spring_boot_library.entity.Book;
import com.luv2code.spring_boot_library.entity.Checkout;
import com.luv2code.spring_boot_library.entity.History;
import com.luv2code.spring_boot_library.responsemodels.ShelfCurrentLoansResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final CheckoutRepository checkoutRepository;
    private final HistoryRepository historyRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    public BookService(BookRepository bookRepository, CheckoutRepository checkoutRepository, HistoryRepository historyRepository) {

        this.bookRepository = bookRepository;
        this.checkoutRepository = checkoutRepository;
        this.historyRepository = historyRepository;
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

        LocalDate now = LocalDate.now();
        LocalDate returnDate = now.plusDays(5);
        Checkout checkout = new Checkout(userEmail, now.toString(), returnDate.toString(), bookId);

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

        List<Checkout> checkouts = checkoutRepository.findByUserEmail(userEmail);
        List<Long> bookIdList = checkouts.stream().map(Checkout::getBookId).toList();
        List<Book> books = bookRepository.findAllById(bookIdList);
        Map<Long, Book> bookMap = books.stream().collect(Collectors.toMap(Book::getId, b -> b));

        List<ShelfCurrentLoansResponse> response = new ArrayList<>();
        String nowString = LocalDate.now().toString();
        for (Checkout checkout : checkouts) {
            Book book = bookMap.get(checkout.getBookId());
            int diff = daysBetweenDates(nowString, checkout.getReturnDate());
            response.add(new ShelfCurrentLoansResponse(book, diff));
        }

        return response;
    }

    private static int daysBetweenDates(String startDate, String endDate) {

        LocalDate startLocalDate = LocalDate.parse(startDate, FORMATTER);
        LocalDate endLocalDate = LocalDate.parse(endDate, FORMATTER);
        return (int) ChronoUnit.DAYS.between(startLocalDate, endLocalDate);
    }

    public void returnBook(String userEmail, Long bookId) throws Exception {

        Optional<Book> bookOption = bookRepository.findById(bookId);
        Checkout validate = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        if (bookOption.isEmpty() || validate == null) {
            throw new Exception("Book does not exist or not checked out by user");
        }
        Book book = bookOption.get();
        book.setCopiesAvailable(book.getCopiesAvailable() + 1);
        bookRepository.save(book);
        checkoutRepository.deleteById(validate.getId());

        History history = new History(
                userEmail,
                validate.getCheckoutDate(),
                LocalDate.now().toString(),
                book.getTitle(),
                book.getAuthor(),
                book.getDescription(),
                book.getImg()
        );
        historyRepository.save(history);
    }

    public void renewLoan(String userEmail, Long bookId) throws Exception {

        Optional<Book> bookOption = bookRepository.findById(bookId);
        Checkout checkout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        if (bookOption.isEmpty() || checkout == null) {
            throw new Exception("Book does not exist or not checked out by user");
        }

        LocalDate today = LocalDate.now();
        LocalDate currentReturnDate = LocalDate.parse(checkout.getReturnDate(), FORMATTER);
        if (!today.isAfter(currentReturnDate)) {
            LocalDate newReturnDate = today.plusDays(7);
            checkout.setReturnDate(newReturnDate.toString());
            checkoutRepository.save(checkout);
        }
    }
}
