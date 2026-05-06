package com.ecommerce.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/error")
public class ErrorServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String message = (String) request.getAttribute("javax.servlet.error.message");

        request.setAttribute("statusCode", statusCode != null ? statusCode : 500);
        request.setAttribute("message", message != null ? message : "An unexpected error occurred");

        request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
    }
}
