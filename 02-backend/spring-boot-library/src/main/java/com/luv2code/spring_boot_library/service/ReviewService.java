package com.luv2code.spring_boot_library.service;

import com.luv2code.spring_boot_library.dao.ReviewRepository;
import com.luv2code.spring_boot_library.entity.Review;
import com.luv2code.spring_boot_library.requestmodels.ReviewRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {

        this.reviewRepository = reviewRepository;
    }

    public void postReview(String userEmail, ReviewRequest reviewRequest) throws Exception {

        Review validateReview = reviewRepository.findByUserEmailAndBookId(userEmail, reviewRequest.getBookId());
        if (validateReview != null) {
            throw new Exception("Already created review");
        }

        Review review = new Review();
        review.setBookId(reviewRequest.getBookId());
        review.setUserEmail(userEmail);
        review.setRating(reviewRequest.getRating());
        review.setReviewDescription(reviewRequest.getReviewDescription().orElse(null));
        review.setDate(Date.valueOf(LocalDate.now()));
        reviewRepository.save(review);
    }

    public Boolean hasUserReview(String userEmail, Long bookId) {

        return reviewRepository.findByUserEmailAndBookId(userEmail, bookId) != null;
    }
}
