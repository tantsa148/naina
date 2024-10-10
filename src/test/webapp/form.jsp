<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Formulaire JSP</title>
</head>
<body>
    <h1>Formulaire utilisateur</h1>
    <form action="without/annotation" method="post">
        <label for="user">String</label>
        <input type="text" name="p.nom" >
        <label for="user">Age</label>
        <input type="number" name="p.age">
        <label for="user">String2</label>
        <input type="text" name="s" >
        <button type="submit">Envoyer</button>
    </form>
</body>

<%
String departements = (String) request.getAttribute("data");%>

<%= departements %>


</html>
