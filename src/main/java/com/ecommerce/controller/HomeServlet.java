package com.ecommerce.controller;

import com.ecommerce.model.Product;
import com.ecommerce.model.Review;
import com.ecommerce.model.User;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.ReviewService;
import com.ecommerce.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(HomeServlet.class);
    private final ProductService productService = new ProductService();
    private final ReviewService reviewService = new ReviewService();
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Product> products = productService.getAllProducts();
        request.setAttribute("products", products);

        // Get user info if logged in
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId != null) {
            User user = userService.getUserById(userId).orElse(null);
            request.setAttribute("currentUser", user);
        }

        // Get recent reviews for display
        request.setAttribute("reviews", reviewService.getAllRecentReviews());

        request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
    }
}
