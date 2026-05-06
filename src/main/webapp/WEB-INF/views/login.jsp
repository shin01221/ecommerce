<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - E-Commerce</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="container">
            <a href="${pageContext.request.contextPath}/" class="logo">E-Commerce</a>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/signup" class="btn btn-primary">Sign Up</a>
            </div>
        </div>
    </nav>

    <div class="container auth-container">
        <div class="auth-card">
            <h2>Login</h2>
            <c:if test="${not empty error}">
                <div class="alert alert-error">${error}</div>
            </c:if>
            <form action="${pageContext.request.contextPath}/login" method="post" class="form">
                <div class="form-group">
                    <label for="username">Username</label>
                    <input type="text" id="username" name="username" required maxlength="30">
                </div>
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required>
                </div>
                <button type="submit" class="btn btn-primary btn-block">Login</button>
            </form>
            <p class="auth-link">Don't have an account? <a href="${pageContext.request.contextPath}/signup">Sign up</a></p>
        </div>
    </div>
</body>
</html>
