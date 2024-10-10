# SipiringyGasy
Pour faire fonctionner le framework :
1-Déclarer le FrontController dans le fichier de configuration web.xml et le mapper comme suit:

<servlet>
    <servlet-name>FrontController</servlet-name>
    <servlet-class>controller.FrontController</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>FrontController</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>

Le choix du <servlet-name> est libre selon votre envie

2-Définir le nom de package contenant les classes controleurs à scanner dans le fichier web.xml comme suit :

<context-param>
    <param-name>scanPackage</param-name>
    <param-value>name.package</param-value> 
</context-param> 

Remplacer name.package par le package contenant vos classes controleurs 

Update: 27 may 2024
- Il est possible de connaitre le nom de controleur et nom de méthode appartenant à l'URL mis par l'utilisateur

Update: 28 may 2024
- Il est possible de visualiser le String retourné par la méthode concernée

Update: 03 jun 2024
- Ajout de la classe ModelView
- Possibilité de dispatch vers une vue avec des données dedans

Update: 04 jun 2024
- Exception levé pour:
	- Un nom de package erroné
	- Doublure d'URL pour le GetMapping

Update: 17 jun 2024
-Possibilité d'invoquer des méthodes contenant des arguments
