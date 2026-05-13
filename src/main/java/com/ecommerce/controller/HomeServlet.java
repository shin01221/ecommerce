package com.ecommerce.controller;

import com.ecommerce.model.User;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.ReviewService;
import com.ecommerce.service.UserService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private final ProductService productService = new ProductService();
    private final ReviewService reviewService = new ReviewService();
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        var products = productService.getAllProducts();
        request.setAttribute("products", products);

        Integer userId = (Integer) request.getAttribute("userId");
        if (userId != null) {
            User user = userService.getUserById(userId).orElse(null);
            request.setAttribute("currentUser", user);
        }

        request.setAttribute("reviews", reviewService.getAllRecentReviews());

        request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
    }
}
