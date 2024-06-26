package org.myspringframework.controller;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Modifier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.*;
import java.net.URL;
import java.io.*;
import java.util.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.myspringframework.util.*;
import org.myspringframework.annotation.*;
import java.net.URL;
import java.net.URLDecoder;
import java.lang.reflect.*;
public class FrontController extends HttpServlet {
    String packagecontroller;
    String prefix;
    String suffix;
    List<Class<?>> controllers = null;
    boolean isChecked=false;
    Map<String, Mapping> listehashMap=null;
    public void init() throws ServletException {
        this.packagecontroller = getServletContext().getInitParameter("packageController");
        this.prefix = getServletContext().getInitParameter("prefix");
        this.suffix = getServletContext().getInitParameter("suffix");
        Fonction f=new Fonction();
        try {
            this.controllers = f.getClassesFromPackage(this.packagecontroller);
            this.listehashMap=f.mapControllersToRoutes(this.controllers);
        } catch (Exception ex) {
            throw new ServletException(ex.getMessage());
        }

    }
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        Fonction f=new Fonction();
        PrintWriter out = response.getWriter();
        String fullUrl = request.getRequestURI();
        String[] parts = fullUrl.split("/");
        String url = "/" + Arrays.stream(parts).skip(2).collect(Collectors.joining("/"));
        Mapping map=f.verifierSiDansHashMap(this.listehashMap,url);
        if(map!=null){
            // Object reponse = f.executeMethod(map.getNomClasse(), map.getNomMethode());
           try{ 
            Object reponse =f.executeMethod(map.getNomClasse(),map.getNomMethode());
            // Object reponse = ReflectionUtils.invokeMethodWithRequestParams(map.getNomClasse(),map.getNomMethode(),request);
//              Method method = clazz.getDeclaredMethod(map.getNomMethode());
//             method.setAccessible(true);
//             Object[] parameters = getParameters(request, method);
//        Object[] params = new Object[parameters.length];
//             for (int i = 0; i < parameters.length; i++) {
//     RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
//     if (requestParam != null) {
//         String paramName = requestParam.value();
//         String paramValue = request.getParameter(paramName);
//         System.out.println("Paramètre : " + paramName + ", Valeur : " + paramValue);
//         params[i] = convertType(paramValue, parameters[i].getType());
//     } else {
//         throw new IllegalArgumentException("Missing RequestParam annotation for parameter: " + parameters[i].getName());
//     }
// }
if (reponse instanceof String) {
                out.println("Le type de retour est String : " + reponse);
            }
            else if (reponse instanceof ModelAndView) {
                ModelAndView rep=(ModelAndView)reponse;
                String urljsp=this.prefix+rep.getUrl()+this.suffix;
                Map<String, Object> data=rep.getData();
                Set<String> keys = data.keySet(); 
                out.println("Url jsp : " + urljsp); 
                for (String key : keys) { // Parcourez chaque clé
                    out.println("Clé trouvée : " + key); 
                    Object value=data.get(key);
                    out.println("Type Data : " + value.getClass().getName()); 
                    out.println("Data : " + value);
                    request.setAttribute(key, value);
                }
                request.getRequestDispatcher(urljsp).forward(request, response);    
            }
            else{
                throw new ServletException("Type de retour not found.");
            }
            }catch(Exception e){
                 try{
            Class<?> clazz = Class.forName(map.getNomClasse());
 Method method = clazz.getDeclaredMethod(map.getNomMethode());
// method.setAccessible(true);

/*Parameter[] parameters = method.getParameters();
Object[] params = new Object[parameters.length];

for (int i = 0; i < parameters.length; i++) {
    RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
    if (requestParam != null) {
        String paramName = requestParam.value();
        String paramValue = request.getParameter(paramName);
        // params[i] = convertType(paramValue, parameters[i].getType());
        out.println(paramName);    
        out.println(paramValue);
    } else {
        // Gérer le cas où le paramètre n'a pas d'annotation RequestParam
        // Ici, vous pouvez définir une valeur par défaut ou gérer autrement
        // Par exemple, vous pouvez lancer une exception pour indiquer qu'un paramètre est manquant
        throw new IllegalArgumentException("Missing RequestParam annotation for parameter: " + parameters[i].getName());
    }
}

// Maintenant que vous avez récupéré tous les paramètres, vous pouvez les utiliser pour invoquer la méthode
// Object result = method.invoke(object, params);
*/
}
catch(Exception ex){
             throw new ServletException(ex.getMessage());
             
}
            }
        }
        else{
                throw new ServletException("URL NOT FOUND.");
        }
         
        
   }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

}

