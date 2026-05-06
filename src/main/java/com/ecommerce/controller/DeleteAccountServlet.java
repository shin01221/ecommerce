package com.ecommerce.controller;

import com.ecommerce.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/delete-account")
public class DeleteAccountServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(DeleteAccountServlet.class);
    private final UserService userService = new UserService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId != null && userService.deleteAccount(userId)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            logger.info("Account deleted for user: {}", userId);
        }
        response.sendRedirect(request.getContextPath() + "/signup");
    }
}
