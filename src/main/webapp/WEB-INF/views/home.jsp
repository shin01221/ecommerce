<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>E-Commerce Home</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="container">
            <a href="${pageContext.request.contextPath}/" class="logo">E-Commerce</a>
            <div class="nav-links">
                <c:choose>
                    <c:when test="${not empty sessionScope.username}">
                        <span class="welcome">Welcome, ${sessionScope.username}</span>
                        <c:if test="${sessionScope.isAdmin}">
                            <span class="badge">Admin</span>
                        </c:if>
                        <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary">Logout</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary">Login</a>
                        <a href="${pageContext.request.contextPath}/signup" class="btn btn-primary">Sign Up</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </nav>

    <div class="container">
        <!-- Add Product Form (Admin only) -->
        <c:if test="${sessionScope.isAdmin}">
            <section class="admin-section">
                <h2>Add New Product</h2>
                <c:if test="${not empty error}">
                    <div class="alert alert-error">${error}</div>
                </c:if>
                <form action="${pageContext.request.contextPath}/products" method="post" class="form">
                    <input type="hidden" name="action" value="add">
                    <div class="form-group">
                        <label for="name">Product Name</label>
                        <input type="text" id="name" name="name" required maxlength="100">
                    </div>
                    <div class="form-group">
                        <label for="description">Description</label>
                        <textarea id="description" name="description" rows="3" maxlength="500"></textarea>
                    </div>
                    <div class="form-group">
                        <label for="price">Price ($)</label>
                        <input type="number" id="price" name="price" step="0.01" min="0.01" required>
                    </div>
                    <div class="form-group">
                        <label for="category">Category</label>
                        <input type="text" id="category" name="category" maxlength="50">
                    </div>
                    <div class="form-group">
                        <label for="stock">Stock</label>
                        <input type="number" id="stock" name="stock" min="0" required>
                    </div>
                    <button type="submit" class="btn btn-primary">Add Product</button>
                </form>
            </section>
        </c:if>

        <!-- Products Section -->
        <section class="products-section">
            <h2>Products</h2>
            <c:if test="${empty products}">
                <p class="no-data">No products available yet.</p>
            </c:if>
            <div class="products-grid">
                <c:forEach var="product" items="${products}">
                    <div class="product-card">
                        <h3><a href="${pageContext.request.contextPath}/product/${product.id}">${product.name}</a></h3>
                        <p class="product-category">${product.category}</p>
                        <p class="product-description">${product.description}</p>
                        <p class="product-price">$<fmt:formatNumber value="${product.price}" type="number" minFractionDigits="2"/></p>
                        <p class="product-stock">Stock: ${product.stock}</p>
                        <c:if test="${sessionScope.isAdmin}">
                            <form action="${pageContext.request.contextPath}/products" method="post" class="delete-form">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="productId" value="${product.id}">
                                <button type="submit" class="btn btn-danger" onclick="return confirm('Delete this product?')">Delete</button>
                            </form>
                        </c:if>
                        <a href="${pageContext.request.contextPath}/product/${product.id}" class="btn btn-secondary">View Details</a>
                    </div>
                </c:forEach>
            </div>
        </section>

        <!-- Reviews Section -->
        <section class="reviews-section">
            <h2>Recent Reviews</h2>
            <c:if test="${empty reviews}">
                <p class="no-data">No reviews yet. Browse products to leave a review!</p>
            </c:if>
            <div class="reviews-list">
                <c:forEach var="review" items="${reviews}">
                    <div class="review-card">
                        <div class="review-header">
                            <span class="review-user">${review.username}</span>
                            <span class="review-rating">
                                <c:forEach begin="1" end="5" var="star">
                                    <c:choose>
                                        <c:when test="${star <= review.rating}">&#9733;</c:when>
                                        <c:otherwise>&#9734;</c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </span>
                        </div>
                        <p class="review-comment">${review.comment}</p>
                        <small class="review-date">${review.createdAt}</small>
                    </div>
                </c:forEach>
            </div>
        </section>

        <!-- Delete Account -->
        <c:if test="${not empty sessionScope.username}">
            <section class="account-section">
                <h2>Account Settings</h2>
                <form action="${pageContext.request.contextPath}/delete-account" method="post" class="delete-account-form">
                    <p>Warning: This action cannot be undone.</p>
                    <button type="submit" class="btn btn-danger" onclick="return confirm('Are you sure you want to delete your account?')">Delete My Account</button>
                </form>
            </section>
        </c:if>
    </div>

    <footer class="footer">
        <p>&copy; 2024 E-Commerce Application</p>
    </footer>
</body>
</html>
