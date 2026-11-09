<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Redirect authenticated users to Dashboard; otherwise redirect to Login page
    if (session != null && session.getAttribute("admin") != null) {
        response.sendRedirect(request.getContextPath() + "/dashboard");
    } else {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
%>

