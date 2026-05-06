package com.ecommerce.dao;

import com.ecommerce.config.DatabaseConfig;
import com.ecommerce.config.RedisConfig;
import com.ecommerce.model.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAO {
    private static final Logger logger = LoggerFactory.getLogger(ProductDAO.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public List<Product> findAll() {
        // Try to get from Redis cache first
        try (Jedis jedis = RedisConfig.getConnection()) {
            String cached = jedis.get("products:all");
            if (cached != null) {
                logger.info("Products retrieved from Redis cache");
                return objectMapper.readValue(cached, new TypeReference<List<Product>>() {});
            }
        } catch (JsonProcessingException e) {
            logger.warn("Failed to parse cached products", e);
        } catch (Exception e) {
            logger.warn("Failed to connect to Redis, falling back to database", e);
        }

        // Get from database
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY created_at DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                products.add(mapProduct(rs));
            }
            // Cache the result in Redis
            cacheProducts(products);
        } catch (SQLException e) {
            logger.error("Error finding all products", e);
        }
        return products;
    }

    public Optional<Product> findById(int id) {
        // Try cache first
        String cacheKey = "product:" + id;
        try (Jedis jedis = RedisConfig.getConnection()) {
            String cached = jedis.get(cacheKey);
            if (cached != null) {
                logger.info("Product {} retrieved from Redis cache", id);
                return Optional.of(objectMapper.readValue(cached, Product.class));
            }
        } catch (Exception e) {
            logger.warn("Failed to get product {} from Redis", id, e);
        }

        // Get from database
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Product product = mapProduct(rs);
                cacheProduct(product);
                return Optional.of(product);
            }
        } catch (SQLException e) {
            logger.error("Error finding product by id: {}", id, e);
        }
        return Optional.empty();
    }

    public boolean create(Product product) {
        String sql = "INSERT INTO products (name, description, price, category, stock) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setDouble(3, product.getPrice());
            stmt.setString(4, product.getCategory());
            stmt.setInt(5, product.getStock());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getInt(1));
                }
                invalidateCache();
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error creating product: {}", product.getName(), e);
        }
        return false;
    }

    public boolean delete(int productId) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            boolean success = stmt.executeUpdate() > 0;
            if (success) {
                invalidateCache();
                try (Jedis jedis = RedisConfig.getConnection()) {
                    jedis.del("product:" + productId);
                } catch (Exception ignored) {}
            }
            return success;
        } catch (SQLException e) {
            logger.error("Error deleting product: {}", productId, e);
        }
        return false;
    }

    private void cacheProducts(List<Product> products) {
        try (Jedis jedis = RedisConfig.getConnection()) {
            jedis.setex("products:all", 300, objectMapper.writeValueAsString(products));
        } catch (Exception e) {
            logger.warn("Failed to cache products in Redis", e);
        }
    }

    private void cacheProduct(Product product) {
        try (Jedis jedis = RedisConfig.getConnection()) {
            jedis.setex("product:" + product.getId(), 300, objectMapper.writeValueAsString(product));
        } catch (Exception e) {
            logger.warn("Failed to cache product {} in Redis", product.getId(), e);
        }
    }

    private void invalidateCache() {
        try (Jedis jedis = RedisConfig.getConnection()) {
            jedis.del("products:all");
        } catch (Exception e) {
            logger.warn("Failed to invalidate product cache", e);
        }
    }

    private Product mapProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getDouble("price"));
        product.setCategory(rs.getString("category"));
        product.setStock(rs.getInt("stock"));
        product.setCreatedAt(rs.getTimestamp("created_at"));
        return product;
    }
}
