package com.ecommerce.service;

import com.ecommerce.dao.UserDAO;
import com.ecommerce.model.User;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.regex.Pattern;

public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final UserDAO userDAO = new UserDAO();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,30}$");

    public String register(String username, String email, String password) {
        if (!validateUsername(username)) return "Invalid username. Use 3-30 letters, numbers, or underscores.";
        if (!validateEmail(email)) return "Invalid email format.";
        if (!validatePassword(password)) return "Password must be at least 6 characters.";

        if (userDAO.findByUsername(username).isPresent()) return "Username already exists.";
        if (userDAO.findByEmail(email).isPresent()) return "Email already exists.";

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User(username, email, hashedPassword);

        if (userDAO.create(user)) {
            logger.info("User registered successfully: {}", username);
            return null;
        }
        return "Registration failed due to a system error. Please try again.";
    }

    public Optional<User> login(String username, String password) {
        Optional<User> optionalUser = userDAO.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (BCrypt.checkpw(password, user.getPasswordHash())) {
                logger.info("User logged in successfully: {}", username);
                return Optional.of(user);
            }
        }
        logger.warn("Failed login attempt for username: {}", username);
        return Optional.empty();
    }

    public boolean deleteAccount(int userId) {
        logger.info("Deleting account for user: {}", userId);
        return userDAO.delete(userId);
    }

    public Optional<User> getUserById(int id) {
        return userDAO.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    private boolean validateUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    private boolean validateEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean validatePassword(String password) {
        return password != null && password.length() >= 6;
    }
}
