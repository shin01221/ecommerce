package com.ecommerce.service;

import com.ecommerce.dao.ReviewDAO;
import com.ecommerce.model.Review;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ReviewService {
    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);
    private static final ReviewDAO reviewDAO = new ReviewDAO();

    public List<Review> getAllRecentReviews() {
        return reviewDAO.findAllRecent();
    }

    public List<Review> getReviewsByProductId(int productId) {
        return reviewDAO.findByProductId(productId);
    }

    public boolean addReview(int productId, int userId, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            logger.warn("Invalid rating: {} for product: {}", rating, productId);
            return false;
        }
        if (comment == null || comment.trim().isEmpty() || comment.length() > 1000) {
            logger.warn("Invalid comment for product: {}", productId);
            return false;
        }

        Review review = new Review(productId, userId, rating, comment);
        boolean success = reviewDAO.create(review);
        if (success) {
            logger.info("Review added for product: {} by user: {}", productId, userId);
        }
        return success;
    }
}
