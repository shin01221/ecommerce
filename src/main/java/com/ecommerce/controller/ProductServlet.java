package com.ecommerce.controller;

import com.ecommerce.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/products/*")
public class ProductServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ProductServlet.class);
    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            try {
                int productId = Integer.parseInt(pathInfo.substring(1));
                request.setAttribute("productId", productId);
                request.getRequestDispatcher("/WEB-INF/views/product-details.jsp").forward(request, response);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Boolean isAdmin = (Boolean) request.getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Boolean sessionAdmin = (Boolean) session.getAttribute("isAdmin");
                if (sessionAdmin != null && sessionAdmin) {
                    isAdmin = true;
                }
            }
        }
        if (isAdmin == null || !isAdmin) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required");
            return;
        }

        String action = request.getParameter("action");

        if ("add".equals(action)) {
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            String priceStr = request.getParameter("price");
            String category = request.getParameter("category");
            String stockStr = request.getParameter("stock");

            if (name == null || name.trim().isEmpty()) {
                request.setAttribute("error", "Product name is required");
                request.setAttribute("showAddForm", true);
                request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
                return;
            }

            try {
                double price = priceStr != null ? Double.parseDouble(priceStr) : 0;
                int stock = stockStr != null ? Integer.parseInt(stockStr) : 0;

                if (price <= 0) {
                    request.setAttribute("error", "Price must be greater than 0");
                    request.setAttribute("showAddForm", true);
                    request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
                    return;
                }
                if (stock < 0) {
                    request.setAttribute("error", "Stock cannot be negative");
                    request.setAttribute("showAddForm", true);
                    request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
                    return;
                }

                if (productService.addProduct(name.trim(), description, price, category, stock)) {
                    response.sendRedirect(request.getContextPath() + "/home");
                } else {
                    request.setAttribute("error", "Failed to add product. Please check your input.");
                    request.setAttribute("showAddForm", true);
                    request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid price or stock value");
                request.setAttribute("showAddForm", true);
                request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
            }
        } else if ("delete".equals(action)) {
            try {
                int productId = Integer.parseInt(request.getParameter("productId"));
                productService.deleteProduct(productId);
                response.sendRedirect(request.getContextPath() + "/home");
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID");
            }
        }
    }
}
