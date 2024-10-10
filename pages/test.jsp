<%@ page contentType="text/json; charset=UTF-8" pageEncoding="UTF-8"%>

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

        .info {
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
    </style>
</head>
<body>
<div class="container">
    <header>
        <h1>Bienvenue sur la Page test</h1>
    </header>
    <main>
        <section class="info">
            <p>Object value : <%= request.getAttribute("data") %></p>
        </section>
    </main>
    <footer>
        Sprint S4
    </footer>
</div>
</body>
</html>
