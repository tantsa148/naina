<%@ page language="java" contentType="text/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="frameworks.MySession" %>
<html>
<head>
    <title>User List</title>
</head>
<body>
    <h2>Your List</h2>
    <ul>
    <% if (request.getAttribute("message") != null) { %>
        <p><%= request.getAttribute("message") %></p>
    <% } %>
    <%
        HashMap<String, List<String>> userLists = new HashMap<>();

        

        List<String> list1 = new ArrayList<>();
        list1.add("David");
        list1.add("20");
        userLists.put("user1", list1);

        List<String> list2 = new ArrayList<>();
        list2.add("Fy");
        list2.add("27");
        userLists.put("user2", list2);

        MySession mySession = new MySession(request.getSession());

        String username = (String) mySession.get("user");

        List<String> userList = userLists.get(username);
        if (userList != null) {
            for (String item : userList) {
    %>
        <li><%= item %></li>
    <%
            }
        }
    %>
    </ul>
    <form action="logout" method="post">
        <input type="submit" value="Logout">
    </form>
</body>
</html>