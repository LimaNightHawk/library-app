package com.luv2code.spring_boot_library.service;

import com.luv2code.spring_boot_library.dao.BookRepository;
import com.luv2code.spring_boot_library.dao.CheckoutRepository;
import com.luv2code.spring_boot_library.dao.ReviewRepository;
import com.luv2code.spring_boot_library.entity.Book;
import com.luv2code.spring_boot_library.requestmodels.AddBookRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AdminService {

    private final BookRepository bookRepository;
    private final CheckoutRepository checkoutRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public AdminService(BookRepository bookRepository, CheckoutRepository checkoutRepository, ReviewRepository reviewRepository) {

        this.bookRepository = bookRepository;
        this.checkoutRepository = checkoutRepository;
        this.reviewRepository = reviewRepository;
    }

    public void updateBookQuantity(Long bookId, int quantity) throws Exception {

        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isEmpty()) {
            throw new Exception("Book not found");
        }

        Book book = optionalBook.get();
        int newTotal = book.getCopies() + quantity;
        int newAvailable = book.getCopiesAvailable() + quantity;
        if (newTotal < 0 || newAvailable < 0) {
            throw new Exception("Invalid book quantity");
        }

        book.setCopies(newTotal);
        book.setCopiesAvailable(newAvailable);
        bookRepository.save(book);
    }

    public void addBook(AddBookRequest bookRequest) {

        Book book = new Book();
        book.setTitle(bookRequest.getTitle());
        book.setAuthor(bookRequest.getAuthor());
        book.setDescription(bookRequest.getDescription());
        book.setCopies(bookRequest.getCopies());
        book.setCopiesAvailable(bookRequest.getCopies());
        book.setTitle(bookRequest.getTitle());
        book.setCategory(bookRequest.getCategory());
        book.setImg(bookRequest.getImg());
        bookRepository.save(book);
    }

    public void deleteBook(Long bookId) throws Exception {

        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isEmpty()) {
            throw new Exception("Book not found to delete");
        }
        bookRepository.delete(optionalBook.get());
        checkoutRepository.deleteAllByBookId(bookId);
        reviewRepository.deleteAllByBookId(bookId);
    }
}
