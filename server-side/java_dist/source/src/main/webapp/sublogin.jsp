<%-- 
    Document   : sublogin
    Created on : Aug 27, 2013, 10:33:52 AM
    Author     : milano
--%>

<%@page import="org.sharif.ce.firerunnerbackends.GPSPointDB"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String username = request.getParameter("username");
    String password = request.getParameter("password");
    
    GPSPointDB db = new GPSPointDB();
    if (!db.isUserValid(username)) {
        response.sendRedirect("login.jsp?invalid=yes");
    }
    else {
        response.sendRedirect(String.format("list.jsp?userid=%s", username));
    }
    
    
%>