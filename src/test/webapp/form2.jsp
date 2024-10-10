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
    <form action="emp/par2" method="post">
        <label for="user">Nom</label>
        <input type="text" name="pers.nom" required>
        <label for="user">Prenom</label>
        <input type="text" name="pers.prenom" >
        <label for="user">Age</label>
        <input type="number" name="pers.age" >
        <label for="user">Number</label>
        <input type="number" name="a" >
        <button type="submit">Envoyer</button>
    </form>

    <%
String departements2 = (String) request.getAttribute("m2");
%>
<%= departements2 %>

</body>
</html>
