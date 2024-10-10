<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="util.Mapping" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Index Page</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            margin: 0;
            padding: 0;
        }

        .container {
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
            background-color: #fff;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        header {
            text-align: center;
            padding: 20px 0;
            border-bottom: 1px solid #ddd;
        }

        header h1 {
            margin: 0;
            color: #333;
        }

        main {
            padding: 20px;
        }

        .info, .controllers {
            margin-bottom: 20px;
        }

        .info p {
            background-color: #e0f7fa;
            padding: 10px;
            border: 1px solid #b2ebf2;
            border-radius: 5px;
            color: #00796b;
        }

        .controllers h2 {
            margin-top: 0;
            color: #00796b;
        }

        .controllers ul {
            list-style-type: none;
            padding: 0;
        }

        .controllers li {
            background-color: #b2ebf2;
            margin: 5px 0;
            padding: 10px;
            border-radius: 5px;
            color: #00796b;
        }

        .mappings {
            margin-bottom: 20px;
        }

        .mappings h2 {
            margin-top: 0;
            color: #00796b;
        }

        .mappings ul {
            list-style-type: none;
            padding: 0;
        }

        .mappings li {
            background-color: #b2ebf2;
            margin: 5px 0;
            padding: 10px;
            border-radius: 5px;
            color: #00796b;
        }

        .val {
            margin-bottom: 20px;
        }

        .val p {
            background-color: #e0f7fa;
            padding: 10px;
            border: 1px solid #b2ebf2;
            border-radius: 5px;
            color: #00796b;
        }

        footer {
            text-align: center;
            padding: 10px 0;
            border-top: 1px solid #ddd;
            margin-top: 20px;
        }

        footer p {
            margin: 0;
            color: #666;
        }

        .login-form {
            margin-bottom: 20px;
        }

        .login-form h2 {
            margin-top: 0;
            color: #00796b;
        }

        .login-form form {
            background-color: #b2ebf2;
            padding: 20px;
            border-radius: 5px;
        }

        .login-form label {
            color: #00796b;
        }

        .login-form input[type="text"],
        .login-form input[type="password"] {
            width: 100%;
            padding: 10px;
            margin: 5px 0;
            border: 1px solid #00796b;
            border-radius: 5px;
            box-sizing: border-box;
        }

        .login-form input[type="submit"] {
            background-color: #00796b;
            color: #fff;
            border: none;
            padding: 10px 20px;
            border-radius: 5px;
            cursor: pointer;
        }

        .login-form input[type="submit"]:hover {
            background-color: #005a4e;
        }

        <!--

        -->

        .emp-form {
            margin-bottom: 20px;
        }

        .emp-form h2 {
            margin-top: 0;
            color: #00796b;
        }

        .emp-form form {
            background-color: #b2ebf2;
            padding: 20px;
            border-radius: 5px;
        }

        .emp-form label {
            color: #00796b;
        }

        .emp-form input[type="text"],
        .emp-form input[type="number"] {
            width: 100%;
            padding: 10px;
            margin: 5px 0;
            border: 1px solid #00796b;
            border-radius: 5px;
            box-sizing: border-box;
        }

        .emp-form input[type="submit"] {
            background-color: #00796b;
            color: #fff;
            border: none;
            padding: 10px 20px;
            border-radius: 5px;
            cursor: pointer;
        }

        .emp-form input[type="submit"]:hover {
            background-color: #005a4e;
        }
    </style>
</head>
<body>
<div class="container">
    <header>
        <h1>Bienvenue sur la Page d'Accueil</h1>
    </header>
    <main>
        <section class="info">
            <p>URL actuelle : <%= request.getAttribute("url") %></p>
        </section>
        <section class="controllers">
            <h2>Liste des Contrôleurs</h2>
            <ul>
                <%
                    List<String> controllers = (List<String>) request.getAttribute("controllers");
                    if (controllers != null) {
                        for (String controller : controllers) {
                            out.println("<li>" + controller + "</li>");
                        }
                    } else {
                        out.println("<li>No controllers found.</li>");
                    }
                %>
            </ul>
        </section>
        <section class="mappings">
            <h2>Mappings URL</h2>
            <ul>
                <%
                    HashMap<String, Mapping> mapping = (HashMap<String, Mapping>) request.getAttribute("hashmap");
                    if (mapping != null) {
                        for (Map.Entry<String, Mapping> entry : mapping.entrySet()) {
                            String url = entry.getKey();
                            Mapping map = entry.getValue();
                            out.println("<ul><li>URL: " + url + "</li><li>Classe: " + map.getClassName() + "</li><li>Méthode: " + map.getMethodName() + "</li></ul>");
                        }
                    } else {
                        out.println("<li>No mappings found.</li>");
                    }
                %>
            </ul>
        </section>
        <section class="val">
            <p>Fonction return : <%= request.getAttribute("value") %></p>
        </section>
        <section class="login-form">
            <h2>Connexion</h2>
            <form action="login" method="post">
                <label for="username">Pseudo:</label><br>
                <input type="text" id="username" name="username"><br>
                <label for="password">Mot de passe:</label><br>
                <input type="password" id="password" name="password"><br><br>
                <input type="submit" value="Se connecter">
            </form>
        </section>
        <section class="emp-form">
            <h2>Liste</h2>
            <form action="liste" method="post">
                <label for="name">Nom:</label><br>
                <input type="text" id="name" name="emp.name"><br>
                <label for="age">Age:</label><br>
                <input type="number" id="age" name="emp.age"><br><br>
                <input type="submit" value="Valider">
            </form>
        </section>
    </main>
    <footer>
        Sprint S4
    </footer>
</div>
</body>
</html>
