<%@ page isErrorPage="true" %>
<html>
<head>
    <title>Erreur</title>
</head>
<body>
    <h1>Une erreur est survenue</h1>
    <p><%= exception.getMessage() %></p>
</body>
</html>
