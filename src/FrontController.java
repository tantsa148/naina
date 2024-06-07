package classes.mvc;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet {
    private String controllerPackage;
    private ScannerController scanner = new ScannerController();
    private Map<String, Mapping> urlMappings;

    public void init(ServletConfig config) throws ServletException {
        try {
            super.init(config);
            ServletContext context = config.getServletContext();
            this.controllerPackage = context.getInitParameter("base_package");
            this.urlMappings = scanner.scanPackage(controllerPackage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void settingAttribute(ModelView mv, HttpServletRequest request) {
        if (mv.getData() instanceof HashMap) {
            HashMap<String, Object> dataMap = (HashMap<String, Object>) mv.getData();

            for (HashMap.Entry<String, Object> entry : dataMap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                request.setAttribute(key, value);
            }
        }
    }


    protected void callMethod(Mapping mapping, PrintWriter out, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Class clazz = Class.forName(mapping.getController());
            Object object = clazz.newInstance(); 
            Method method = clazz.getMethod(mapping.getMethod());
            method.setAccessible(true);
    
            Object result = method.invoke(object);
    
            if (result instanceof String) {
                String resultString = (String) result;
                out.println("<li>Results: " + resultString + "</li>");
                out.println("<li>Type: " + resultString.getClass().getName() + "</li>");
            } else if (result instanceof ModelView) {
                ModelView modelView = (ModelView) result;
                out.println("<li>Type: " + modelView.getClass().getName() + "</li>");
                settingAttribute(modelView, request);
                RequestDispatcher dispatcher = request.getRequestDispatcher(modelView.getUrl());
                dispatcher.forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace(response.getWriter());
        }
    }
    

    protected void validateMapping(Mapping mapping, PrintWriter out, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestURI = request.getRequestURI();
        if (mapping != null) {
            out.println("<h2>Mapping Found:</h2>");
            out.println("<ul>");
            out.println("<li>Controller: " + mapping.getController() + "</li>");
            out.println("<li>Method: " + mapping.getMethod() + "</li>");

            try {
                this.callMethod(mapping, out, request, response);
            } catch (Exception e) {
                e.printStackTrace(response.getWriter());
            }

            out.println("</ul>");
        } else {
            out.println("<h2>No Mapping Found for URL: " + requestURI + "</h2>");
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        String requestURI = request.getRequestURI();
        Mapping mapping = this.urlMappings.get(requestURI);
        PrintWriter out = response.getWriter();
        out.println("<h1>Hello, World!</h1>");
        out.println("<h1>LINK : " + request.getRequestURL() + "</h1>");
        out.println("<h2>List of Controllers and Methods:</h2>");
        out.println("<ul>");
        for (Map.Entry<String, Mapping> entry : urlMappings.entrySet()) {
            out.println("<li>URL: " + entry.getKey() + ", Controller: " + entry.getValue().getController() + ", Method: " + entry.getValue().getMethod() + "</li>");
        }
        out.println("</ul>");
        this.validateMapping(mapping, out, request, response);
    }
    

   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      this.processRequest(request, response);
   }

   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      this.processRequest(request, response);
   }
}
