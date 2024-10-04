package com.luv2code.spring_boot_library.controller;

import com.luv2code.spring_boot_library.requestmodels.ReviewRequest;
import com.luv2code.spring_boot_library.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/reviews")
public class ReviewController extends AbstractAuthorizationController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {

        this.reviewService = reviewService;
    }

    @GetMapping("/secure")
    public Boolean getReview(@RequestParam Long bookId, @RequestHeader(value = "Authorization") String token) throws Exception {

        String userEmail = getUserEmailFromToken(token);
        if (userEmail == null) {
            throw new Exception("User email cannot be null");
        }
        return reviewService.hasUserReview(userEmail, bookId);
    }

    @PostMapping("/secure")
    public void postReview(@RequestBody ReviewRequest reviewRequest, @RequestHeader(value = "Authorization") String token) throws Exception {

        String userEmail = getUserEmailFromToken(token);
        if (userEmail == null) {
            throw new Exception("User email cannot be null");
        }
        reviewService.postReview(userEmail, reviewRequest);
    }
}
