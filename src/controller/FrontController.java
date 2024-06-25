package mg.ituprom16.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ModuleLayer.Controller;
import java.util.Vector;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.ituprom16.annotation.*;

public class FrontController extends HttpServlet{

    private String packageSource;
    private Vector<Class> lsController;
    private boolean check = false;
    public void init() throws ServletException{
        try
        {
            this.packageSource = this.getInitParameter("package-source");
        }
        catch(Exception e)
        {
            System.out.println("Error Init package source");
            e.printStackTrace();
        }
    }

    public void getControllers() throws Exception
    {
        ServletContext context = getServletContext();
        String classpath = context.getResource(this.packageSource).getPath();
        String namePackage = packageSource.split("classes/")[1].replace("/", ".");
        File classpathDirectory = new File(classpath);

        this.lsController = new Vector<Class>();
        File[] listeFiles = classpathDirectory.listFiles();

        for(File file : listeFiles)
        {   
            if (file.isFile() && file.getName().endsWith(".class")) {
                String className = file.getName().substring(0,file.getName().length()-6);
                Class clazz = Thread.currentThread().getContextClassLoader().loadClass(namePackage+"."+className);
                if (clazz.isAnnotationPresent( mg.ituprom16.annotation.Controller.class)) {
                    this.lsController.add(clazz);
                }
            }
        }
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       processRequest(req, resp);
    }

    public void processRequest(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{

        PrintWriter writer = resp.getWriter();
        if (this.check==false) {
            try {
                getControllers();
                this.check = true;
            } catch (Exception e) {
                writer.print(e.getMessage());
            }   
        }  
        for (int i = 0; i < this.lsController.size(); i++) {
            writer.print(lsController.get(i).getSimpleName()+"\n");
        }
    }
}
