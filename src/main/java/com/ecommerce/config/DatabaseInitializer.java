package com.ecommerce.config;

import com.ecommerce.dao.UserDAO;
import com.ecommerce.model.User;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class DatabaseInitializer implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);
    private static final int MAX_RETRIES = 10;
    private static final long RETRY_DELAY_MS = 2000;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Initializing database and seeding admin user...");

        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                UserDAO userDAO = new UserDAO();
                if (userDAO.findByUsername("admin").isEmpty()) {
                    String hashedPassword = BCrypt.hashpw("admin123", BCrypt.gensalt());
                    User admin = new User("admin", "admin@ecommerce.com", hashedPassword);
                    admin.setAdmin(true);
                    if (userDAO.create(admin)) {
                        logger.info("Admin user created successfully");
                    } else {
                        logger.error("Failed to create admin user");
                    }
                } else {
                    logger.info("Admin user already exists");
                }
                return;
            } catch (Exception e) {
                logger.warn("Database not ready (attempt {}/{}): {}", i + 1, MAX_RETRIES, e.getMessage());
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        logger.error("Failed to initialize admin user after {} attempts", MAX_RETRIES);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
