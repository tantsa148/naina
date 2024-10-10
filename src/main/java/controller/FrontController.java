/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import stereotype.Controller;
import rooting.Mapping;
import rooting.VerbAction;
import java.io.File;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import annotation.Url;
import annotation.ModelAttribute;
import annotation.RequestParam;
import java.lang.reflect.Parameter;
import javax.servlet.RequestDispatcher;
import servlet.ModelView;
import servlet.support.SessionAttribute;
import java.lang.reflect.Field;
import javax.servlet.http.HttpSession;
import com.google.gson.Gson;

/**
 *
 * @author itu
 */
public class FrontController extends HttpServlet {

    // Nom du package à scanner, défini dans le web.xml
    private String packageName; 
    //Liste des classes annptés en tant que Controller
    List<Class<?>> controllerClasses;
    //Permet de stocker chaque URL à un Controleur et une Methode
    HashMap<VerbAction, Mapping> routingMap;
    //Stocke les types d'aguments par méthode
    HashMap<Mapping, Class<?>[]> argsMethodMap;
    //Stocke les noms d'arguments par méthode
    HashMap<Mapping, String[]> argsNameMethodMap;
    //Les URL des ServletMapping
    String[] servletMappLs = {"ctl/","FrontController/"};

    @Override
    //Initialise le Servlet
    public void init() throws ServletException {
        super.init();
        
        //Prend le nom de package à scanner dans le web.xml
        packageName = getServletContext().getInitParameter("scanPackage");
        
        try {
            //Scanne les controleurs
            controllerClasses = scanControllers(packageName);
        } catch (IOException ex) {
            //Renvoie une exception si le package indiqué n'existe pas
            throw new ServletException("Error scanning controllers: " + ex.getMessage());
        } catch (ServletException ex){
            throw new Error("Error scanning controllers: " + ex.getMessage());
        }
        //Initialise le routingMap
        routingMap = new HashMap<>();
        //Initialise le argsMethodMap
        argsMethodMap = new HashMap<>();
        //Initialise le argsNameMethodMap
        argsNameMethodMap = new HashMap<>();
        
        // Itérer sur les classes contrôleur
        for (Class<?> controllerClass : controllerClasses) {
            //Si la classe est annoté en tant que @Controller
            if (controllerClass.isAnnotationPresent(Controller.class)) {
                // Rechercher les méthodes annotées avec @RequestMapping
                Method[] methods = controllerClass.getDeclaredMethods();
                for (Method method : methods) {
                    //method.isAnnotationPresent(Controller.class) && 
                    if (method.isAnnotationPresent(Url.class)) {

                        String mappingPath = method.getAnnotation(Url.class).action();
                        String verb = method.getAnnotation(Url.class).verb();
                        VerbAction verbAction = new VerbAction(mappingPath,verb);
                        Mapping mapping = new Mapping(controllerClass.getSimpleName(), method.getName());
                        
                        // Check for duplicate method names based on method name
                        if (routingMap.containsKey(verbAction)) {
                            Mapping existingMapping = routingMap.get(verbAction);
                            if (existingMapping.getMethodName().equals(method.getName()) && verbAction.getVerb().equals(verb)) {
                                throw new ServletException("Superposition d'URL: " + mappingPath + " pour la méthode "+existingMapping.getClassName()+"."+existingMapping.getMethodName() +"() et "+controllerClass.getSimpleName()+"."+ method.getName()+"()");
                            }
                        }
                        
                        // Get parameter types
                        Class<?>[] parameterTypes = method.getParameterTypes();
                       
                        // Add method and parameter types to the map
                        argsMethodMap.put(mapping, parameterTypes);
                        
                        // Add method and parameter names to the map
                        //argsNameMethodMap.put(mapping, paramsName);
                        System.out.println(mappingPath+" "+controllerClass.getSimpleName()+" "+method.getName()+" "+verb);
                        routingMap.put(verbAction, mapping);
                    }
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String requestURI = req.getRequestURI(); // Obtenir l'URI de la requête
            String contextPath = req.getContextPath(); // Obtenir le chemin d'accès au contexte de l'application
            String remainingURI = requestURI.substring(contextPath.length()); // Supprimer le chemin d'accès au contexte

            StringBuilder newRemainingURI = new StringBuilder(remainingURI);

            for (String elementToDelete : servletMappLs) {
                if (remainingURI.contains(elementToDelete)) {
                    int index = remainingURI.indexOf(elementToDelete);
                    newRemainingURI.delete(index, index + elementToDelete.length());
                }
            }
            
            //On obtient que l'URL de la méthode
            remainingURI = newRemainingURI.toString();

            String httpMethodUsed = req.getMethod();
            System.out.println("method used:"+httpMethodUsed+httpMethodUsed.length());
            
            //Boucle chaque méthode et chaque Controller
            for (Map.Entry<VerbAction, Mapping> entry : routingMap.entrySet()) {
                VerbAction verbAction = entry.getKey();
                String mappingPath = verbAction.getAction();
                String verbMethod = verbAction.getVerb();
                System.out.println("Methode verb:"+verbMethod+verbMethod.length());
                if (remainingURI.equals(mappingPath) && httpMethodUsed.equals(verbMethod)) {
                    String className = entry.getValue().getClassName();
                    Class<?> myClass = Class.forName(packageName+"."+className);
                    String methodName = entry.getValue().getMethodName();

                    //Prend les types d'args de la méthode
                    Class<?>[] parameterTypes = argsMethodMap.get(entry.getValue());
                    //Declare la méthode avec les args
                    Method myMethod = myClass.getMethod(methodName, parameterTypes);

                    //Prend touts les args de la méthode
                    Parameter[] parameters = myMethod.getParameters();
                    //Init le tableau d'args pour la méthode
                    Object[] arguments = new Object[parameterTypes.length];

                    // Prepare arguments based on parameter types
                    for (int i = 0; i < parameters.length; i++) {
                        Parameter parameter = parameters[i];
                        System.out.println(parameter.getName());
                        Class<?> parameterType = parameter.getType();
                        //Assigne une valeur par défaut (null) à l'argument
                        arguments[i] = getDefaultValueArgs(parameterType);

                        //Annotation d'arg 
                        RequestParam reqParamAnnot = parameter.getAnnotation(RequestParam.class);
                        //Si l'reqParamAnnot est présente dans l'aegument
                        if (reqParamAnnot != null) {
                            System.out.println("misy annot prim");
                            //Prend le nom de l'argument
                            String annotationValue = reqParamAnnot.value();
                            //Prend la valeur dans le request
                            String requestParameterName = req.getParameter(annotationValue);

                            //S'il existe alors on le prend et on le convertit 
                            if (!requestParameterName.isEmpty() || requestParameterName!=null) {
                                if(parameterType.isPrimitive()){
                                    // Convert request parameter value based on parameter type
                                    arguments[i] = convertRequestParameterValue(requestParameterName, parameterType);
                                } else if (parameterType.isAssignableFrom(String.class)){
                                    arguments[i] = requestParameterName;
                                }
                            } 
                        }
                        //Si l'argument ne présente pas d'arguments, on throw une Excpetion
                        if(reqParamAnnot == null){
                            if(parameterType.isPrimitive() || parameterType.isAssignableFrom(String.class)){
                                //Prend le nom de l'argument
                                //String paramValue = parameterNames[i];
                                String paramValue = parameter.getName();

                                System.out.println(paramValue);
                     
                                //Prend la valeur dans le request
                                String requestParameterName = req.getParameter(paramValue);

                                //System.out.println(requestParameterName);

                                //S'il existe alors on le prend et on le convertit 
                                if (!requestParameterName.isEmpty() || requestParameterName!=null) {
                                    // Convert request parameter value based on parameter type
                                    if(parameterType.isPrimitive()){
                                        // Convert request parameter value based on parameter type
                                        arguments[i] = convertRequestParameterValue(requestParameterName, parameterType);
                                    } else if (parameterType.isAssignableFrom(String.class)){
                                        arguments[i] = requestParameterName;
                                    }
                                }
                            }
                        }
                        Enumeration<String> formparameterNames = req.getParameterNames();
                        ModelAttribute modelAnnot = parameter.getAnnotation(ModelAttribute.class);
                        if(modelAnnot != null && !parameterType.isPrimitive() && !parameterType.isAssignableFrom(String.class)){
                            System.out.println("misy annot obj");
                            //Prend le nom de l'argument
                            String annotationValue = modelAnnot.value();
                            //Instancie l'objet arguument
                            Object o = parameterType.newInstance();
                            Class<?> classe = o.getClass();
                            Field[] champs = classe.getDeclaredFields();

                            //Assigne des valeurs par défaut à touts les attributs
                            ////setFieldsToNull(o,champs);

                            for (Field champ : champs) {
                                champ.setAccessible(true); // Allow access to private fields
                                if (champ.getType().equals(int.class)) {
                                    champ.set(o, 0);
                                } else if (champ.getType().equals(double.class)) {
                                    champ.set(o, 0.0);
                                } else if (champ.getType().equals(boolean.class)) {
                                    champ.set(o, false);
                                } else if (champ.getType().equals(String.class)) {
                                    champ.set(o, "");
                                } else {
                                    champ.set(o, null);
                                }
                            }
            
                            //Prend la valeur dans le request
                            while (formparameterNames.hasMoreElements()) {
                                String requestParameterName = formparameterNames.nextElement();
                                String[] parts = requestParameterName.split("\\.");  
                                if (parts.length > 1) {
                                    String objName = parts[0];
                                    if(annotationValue.equals(objName)) {
                                        String attributeName = parts[1];
                                        
                                        for (Field champ : champs) {
                                            champ.setAccessible(true);
                                            String fieldName = champ.getName();
                                            if(fieldName.equals(attributeName)) {
                                                String rep=req.getParameter(requestParameterName);

                                                if (!rep.isEmpty() || req!=null) {
                                                    if (champ.getType().equals(int.class)) {
                                                        champ.set(o, Integer.parseInt(rep));
                                                    } else if (champ.getType().equals(double.class)) {
                                                        champ.set(o, Double.parseDouble(rep));
                                                    //} else if (champ.getType().equals(boolean.class)) {
                                                        //champ.set(o, false);
                                                    } else if (champ.getType().equals(String.class)) {
                                                        champ.set(o, rep);
                                                    }
                                                }
                                            }
                                        }
                                    } 
                                }
                            }  
                            arguments[i] = o;
                        }
                        
                        
                        if(modelAnnot == null && !parameterType.isPrimitive() && !parameterType.isAssignableFrom(String.class)){
                            //Prend le nom de l'argument
                            //String paramValue = parameterNames[i];
                            String paramValue = parameter.getName();
                            System.out.println(" tsisy annot obj");
                            Object o = parameterType.newInstance();
                            Class<?> classe = o.getClass();
                            Field[] champs = classe.getDeclaredFields();

                            //Assigne des valeurs par défaut à touts les attributs
                            //setFieldsToNull(o,champs);

                            for (Field champ : champs) {
                                champ.setAccessible(true); // Allow access to private fields
                                if (champ.getType().equals(int.class)) {
                                    champ.set(o, 0);
                                } else if (champ.getType().equals(double.class)) {
                                    champ.set(o, 0.0);
                                } else if (champ.getType().equals(boolean.class)) {
                                    champ.set(o, false);
                                } else if (champ.getType().equals(String.class)) {
                                    champ.set(o, "");
                                } else {
                                    champ.set(o, null);
                                }
                            }
            
                            //Prend la valeur dans le request
                            while (formparameterNames.hasMoreElements()) {
                                String requestParameterName = formparameterNames.nextElement();
                                String[] parts = requestParameterName.split("\\.");  
                                if (parts.length > 1) {
                                    String objName = parts[0];
                                    if(paramValue.equals(objName)) {
                                        String attributeName = parts[1];
                                        
                                        for (Field champ : champs) {
                                            champ.setAccessible(true);
                                            String fieldName = champ.getName();
                                            if(fieldName.equals(attributeName)){
                                                String rep=req.getParameter(requestParameterName);

                                                if (!rep.isEmpty() || req!=null) {
                                                    if (champ.getType().equals(int.class)) {
                                                        champ.set(o, Integer.parseInt(rep));
                                                    } else if (champ.getType().equals(double.class)) {
                                                        champ.set(o, Double.parseDouble(rep));
                                                    //} else if (champ.getType().equals(boolean.class)) {
                                                        //champ.set(o, false);
                                                    } else if (champ.getType().equals(String.class)) {
                                                        champ.set(o, rep);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }  
                            arguments[i] = o;
                        }
                    }

                    //Instancie la classe
                    Object instance = myClass.newInstance();
                    Field[] fields = myClass.getDeclaredFields();
                    /////for (Field field : fields) {
                        /////Class<?> fieldType = field.getType();
                        /////if (fieldType.equals(SessionAttribute.class)) {
                            // Le champ est de type Object
                            /////System.out.println("Champ trouvé : " + field.getName());
                        /////}
                    /////}

                    SessionAttribute sessionVar = null; // Déclaration de la variable sessionVar
                    HashMap<String, Object> sessionMap = new HashMap<>(); // Initialisation de sessionMap
                    HashMap<String, Object> sessionMapBackup = new HashMap<>();

                    // Récupération de la HttpSession
                    HttpSession httpSession = req.getSession();
                    //httpSession.invalidate();

                    // Itération des attributs de la HttpSession
                    Enumeration<String> attributeNames = httpSession.getAttributeNames();
                    while (attributeNames.hasMoreElements()) {
                      String attributeName = attributeNames.nextElement();
                      Object attributeValue = httpSession.getAttribute(attributeName);

                      System.out.println("Session "+attributeName+" "+attributeValue);

                      // Stockage des attributs dans la sessionMap
                      sessionMap.put(attributeName, attributeValue);
                      sessionMapBackup.put(attributeName, attributeValue);
                    }

                    for (Field field : fields) {
                        Class<?> fieldType = field.getType();
                        if (fieldType.equals(SessionAttribute.class)) {
                            // Le champ est de type SessionAttribute
                            System.out.println("Champ trouvé : " + field.getName());

                            // Récupération de la valeur du champ
                            field.setAccessible(true); // Permet l'accès au champ privé
                            try {
                                sessionVar = (SessionAttribute) field.get(instance);
                                // Fusion de sessionMap avec le HashMap dans sessionVar
                                if (sessionVar != null) {
                                    sessionMap.putAll(sessionVar.getData());
                                    sessionMapBackup.putAll(sessionVar.getData());
                                    sessionVar.setData(sessionMap);
                                    System.out.println("Fusion de sessionMap avec sessionVar effectuée");
                                }

                                field.set(instance, sessionVar); // Assigner la valeur au champ de l'instance
                            } catch (IllegalAccessException e) {
                              e.printStackTrace();
                            }
                        }
                    }

                    //Invoke la méthode
                    Object result = myMethod.invoke(instance,arguments);

                    for (Field field : fields) {
                        Class<?> fieldType = field.getType();
                        if (fieldType.equals(SessionAttribute.class)) {
                            field.setAccessible(true);
                            try {
                                SessionAttribute sessionVar2 = (SessionAttribute) field.get(instance);

                                if (sessionVar2 != null) {
                                    // Retrieve data from SessionAttribute (assuming it's modifiable)
                                    HashMap<String, Object> updatedData = sessionVar2.getData();

                                    // Assuming 'updatedData' and 'sessionMap' are HashMaps
                                    for (Map.Entry<String, Object> entry7 : updatedData.entrySet()) {
                                        String key = entry7.getKey();
                                        Object value = entry7.getValue();

                                        System.out.println(key+" value "+value);

                                        // Update or add the key-value pair in sessionMap
                                        sessionMap.put(key, value);
                                        sessionMapBackup.put(key, value);
                                        sessionVar2.setData(sessionMap);
                                        httpSession.setAttribute(key, value);
                                    }

                                    // Optional: Remove keys from sessionMap that are not present in updatedData
                                    for (Map.Entry<String, Object> entry5 : sessionMapBackup.entrySet()) {
                                        String key = entry5.getKey();
                                        System.out.println(key);
                                        if (!updatedData.containsKey(key)) {
                                            System.out.println("on remove");
                                            sessionMap.remove(key);
                                            sessionVar2.setData(sessionMap);
                                            httpSession.removeAttribute(key);
                                        }
                                    }//


                                  sessionVar2.setData(sessionMap);
                                }




                                field.set(instance, sessionVar2); // Assigner la valeur au champ de l'instance
                            } catch (IllegalAccessException e) {
                              e.printStackTrace();
                            }
                        }
                    }

                    Gson gson = new Gson();

                    String verbUsed = myMethod.getAnnotation(Url.class).verb();

                   
                        if (result instanceof ModelView){
                            //Cast le resultat en ModelView
                            ModelView modelView = (ModelView) result;
                            try {
                                // Récupère les données de ModelView
                                Map<String, Object> data = modelView.getData();

                                req.setAttribute("data", gson.toJson(data));

                                // Obtient l'URL de la vue à partir de ModelView
                                String url = modelView.getUrl();
                                // Utilise un RequestDispatcher pour transférer la requête vers l'URL spécifiée
                                RequestDispatcher dispatcher = req.getRequestDispatcher(url);
                                dispatcher.forward(req, resp);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return; // Retourne après avoir traité le cas ModelView
                        } else {
                            resp.setContentType("application/json");
                            resp.setCharacterEncoding("UTF-8");
                            resp.getWriter().write(gson.toJson(result));
                            return;
                        }
                } 
            }

            // Si aucune correspondance n'est trouvée, envoyer une réponse appropriée
            // Simuler une ressource non trouvée
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);

            // Obtenir le PrintWriter
            PrintWriter out = resp.getWriter();

            // Écrire la page 404
            out.println("<html>");
            out.println("<head><title>Erreur 404 - Page non trouvée</title></head>");
            out.println("<body>");
            out.println("<h1>Erreur 404</h1>");
            out.println("<p>"+"Mapping non trouve pour l'URI : " + remainingURI+" avec la methode "+httpMethodUsed+"</p>");
            out.println("</body>");
            out.println("</html>");
            
        } catch (IOException | ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException | ServletException e) {   
        }
    }

    //méthode pour scanner les controllers annotés en Controller
    private List<Class<?>> scanControllers(String packageName) throws IOException, ServletException {
        //Initialise la variable de retour
        List<Class<?>> controllerClasses1 = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        //Retrace le path du package 
        String path = packageName.replace(".", "/");
        Enumeration<URL> resources = classLoader.getResources(path);
        String directoryPath = null;
        
        // Check if resources exist
        if (resources.hasMoreElements()) {
            while (resources.hasMoreElements()) {
                // Replace with your actual directory path
                URL resource = resources.nextElement();
                directoryPath = resource.getPath().substring(1);
            }
            
            List<File> files = listFiles(new File(directoryPath));
            for (File file : files) {
                System.out.println(file.getAbsolutePath());
                if (file.getAbsolutePath().endsWith(".class")) {
                    // Split the path into segments using the backslash ('\') character as the delimiter
                    String[] pathSegments = file.getAbsolutePath().split("\\\\");

                    // Find the index of the segment containing the class name (usually the last segment)
                    int classIndex = pathSegments.length - 1;

                    // Check if the last segment contains the class name extension (".class")
                    if (!pathSegments[classIndex].endsWith(".class")) {
                        throw new IllegalArgumentException("Invalid file path: " + file.getAbsolutePath());
                    }

                    // Extract the class name by removing the extension and any additional dot (".") before it
                    String className = pathSegments[classIndex].substring(0, pathSegments[classIndex].lastIndexOf("."));

                    Class<?> controllerClass;
                    try {
                        controllerClass = Class.forName(packageName + "." + className, true, classLoader); // Use current class loader
                    } catch (ClassNotFoundException ex) {
                        System.err.println("Error loading class: " + className);
                        continue;
                    }
                    controllerClasses1.add(controllerClass);
                }
            }

        } else {
          // Path does not exist, handle the case
          throw new Error("Package name 'scanPackage' as \""+packageName+"\" not found in web.xml initialization parameter.");
        }
        
        return controllerClasses1;
    }
    
    private static List<File> listFiles(File directory) throws IOException {
        List<File> files = new ArrayList<>();
        if (directory.isDirectory()) {
            File[] subfiles = directory.listFiles();
            for (File subfile : subfiles) {
                if (subfile.isDirectory()) {
                    files.addAll(listFiles(subfile));
                } else {
                    files.add(subfile);
                }
            }
        }
        return files;
    }
    
    private static Object getDefaultValueArgs(Class<?> type) throws InstantiationException, IllegalAccessException {
        if (type == int.class || type == Integer.class) {
            return 0;
        } else if (type == double.class || type == Double.class) {
            return 0.0;
        } else if (type == String.class) {
            return "";
        } else if (type == boolean.class || type == Boolean.class) {
            return false;
        } else if (type == Object.class) {
            return null;
        } else {
            Object o = type.newInstance();
            Class<?> classe = o.getClass();
            Field[] champs = classe.getDeclaredFields();

            //setFieldsToNull(o, champs);
            for (Field champ : champs) {
                champ.setAccessible(true); // Allow access to private fields
                if (champ.getType().equals(int.class)) {
                    champ.set(o, 0);
                } else if (champ.getType().equals(double.class)) {
                    champ.set(o, 0.0);
                } else if (champ.getType().equals(boolean.class)) {
                    champ.set(o, false);
                } else if (champ.getType().equals(String.class)) {
                    champ.set(o, "");
                } else {
                    champ.set(o, null);
                }
            }
            return o;
        }
    }
    
    private Object convertRequestParameterValue(String requestParameterValue, Class<?> parameterType) {
        if (parameterType == int.class || parameterType == Integer.class) {
            return Integer.parseInt(requestParameterValue);
        } else if (parameterType == double.class || parameterType == Double.class) {
            return Double.parseDouble(requestParameterValue);
        } else if (parameterType == boolean.class || parameterType == Boolean.class) {
            return Boolean.parseBoolean(requestParameterValue);
        } else if (requestParameterValue.equals("")) {
            return 0;
        } else {
            // Handle other types (if applicable)
            return null;
        }
        //Ajouter le temps
    }

    private static void setFieldsToNull(Object o, Field[] champs) throws IllegalArgumentException, IllegalAccessException{
        for (Field champ : champs) {
            champ.setAccessible(true); // Allow access to private fields
            if (champ.getType().equals(int.class)) {
                champ.set(o, 0);
            } else if (champ.getType().equals(double.class)) {
                champ.set(o, 0.0);
            } else if (champ.getType().equals(boolean.class)) {
                champ.set(o, false);
            } else if (champ.getType().equals(String.class)) {
                champ.set(o, "");
            } else {
                champ.set(o, null);
            }
        }
    }

    private void synchronizeSessionMap(HttpSession httpSession,HashMap<String, Object> sessionMap, HashMap<String, Object> updatedData) {
        // Identify elements added to updatedData
        Set<String> addedKeys = new HashSet<>(updatedData.keySet());
        addedKeys.removeAll(sessionMap.keySet());

        // Add missing elements from updatedData to sessionMap
        for (String key : addedKeys) {
            sessionMap.put(key, updatedData.get(key));
            httpSession.setAttribute(key, updatedData.get(key));
        }

        // Identify elements removed from sessionMap
        Set<String> removedKeys = new HashSet<>(sessionMap.keySet());
        removedKeys.removeAll(updatedData.keySet());

        // Remove stale elements from sessionMap
        for (String key : removedKeys) {
            sessionMap.remove(key);
            httpSession.removeAttribute(key);
        }
    }


}


