package com.ecommerce.service;

import com.ecommerce.dao.ProductDAO;
import com.ecommerce.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private static final ProductDAO productDAO = new ProductDAO();

    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }

    public Optional<Product> getProductById(int id) {
        return productDAO.findById(id);
    }

    public boolean addProduct(String name, String description, double price, String category, int stock) {
        if (!validateProductName(name)) {
            logger.warn("Invalid product name: {}", name);
            return false;
        }
        if (price <= 0) {
            logger.warn("Invalid product price: {}", price);
            return false;
        }
        if (stock < 0) {
            logger.warn("Invalid product stock: {}", stock);
            return false;
        }

        Product product = new Product(name, description, price, category, stock);
        boolean success = productDAO.create(product);
        if (success) {
            logger.info("Product created: {}", name);
        }
        return success;
    }

    public boolean deleteProduct(int productId) {
        boolean success = productDAO.delete(productId);
        if (success) {
            logger.info("Product deleted: {}", productId);
        }
        return success;
    }

    private boolean validateProductName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() <= 100;
    }
}
