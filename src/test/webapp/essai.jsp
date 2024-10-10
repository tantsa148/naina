<%-- 
    Document   : essai
    Created on : Jun 3, 2024, 2:18:52 PM
    Author     : itu
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
String departements = (String) request.getAttribute("coucou");%>
<h1>bienvenue sur la vue kodiny</h1> 

<%
String departements2 = (String) request.getAttribute("m3");
%>
<%= departements2 %>
