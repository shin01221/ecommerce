<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${product.name} - E-Commerce</title>
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
        <a href="${pageContext.request.contextPath}/" class="back-link">&larr; Back to Products</a>

        <section class="product-details">
            <h1>${product.name}</h1>
            <p class="product-category">Category: ${product.category}</p>
            <p class="product-description">${product.description}</p>
            <p class="product-price">$<fmt:formatNumber value="${product.price}" type="number" minFractionDigits="2"/></p>
            <p class="product-stock">In Stock: ${product.stock}</p>
        </section>

        <!-- Reviews Section -->
        <section class="reviews-section">
            <h2>Reviews</h2>

            <!-- Add Review Form -->
            <c:if test="${not empty sessionScope.userId}">
                <c:if test="${not empty error}">
                    <div class="alert alert-error">${error}</div>
                </c:if>
                <form action="${pageContext.request.contextPath}/review" method="post" class="form review-form">
                    <input type="hidden" name="productId" value="${product.id}">
                    <div class="form-group">
                        <label for="rating">Rating</label>
                        <select id="rating" name="rating" required>
                            <option value="5">5 - Excellent</option>
                            <option value="4">4 - Very Good</option>
                            <option value="3">3 - Good</option>
                            <option value="2">2 - Fair</option>
                            <option value="1">1 - Poor</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="comment">Comment</label>
                        <textarea id="comment" name="comment" rows="4" required maxlength="1000"></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary">Submit Review</button>
                </form>
            </c:if>
            <c:if test="${empty sessionScope.userId}">
                <p><a href="${pageContext.request.contextPath}/login">Login</a> to leave a review.</p>
            </c:if>

            <!-- Reviews List -->
            <c:if test="${empty reviews}">
                <p class="no-data">No reviews yet. Be the first to review this product!</p>
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
    </div>

    <footer class="footer">
        <p>&copy; 2024 E-Commerce Application</p>
    </footer>
</body>
</html>
