package mg.ituprom16.controller;

import java.io.File;
import java.io.PrintWriter;
import java.text.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;

public class PackageUtils {
    public List<Class <?> > getAllClasses(String packageSource) throws Exception
    {
        String classpath = packageSource.replace(".","/");
        File classpathDirectory = new File(classpath);
        File[] listeFiles = classpathDirectory.listFiles();
        List<Class <?>> toReturn = new ArrayList<>(); 
        if (classpathDirectory.exists()) {
            for(File file : listeFiles)
            {   
                if (file.isFile() && file.getName().endsWith(".class")) {
                    String className = file.getName().substring(0,file.getName().length()-6);
                    Class clazz = Thread.currentThread().getContextClassLoader().loadClass(packageSource+"."+className);
                    toReturn.add(clazz);
                }
            }   
        }
        return toReturn;
    }
    public List<Class <?> > getClassesByAnnotation(String packageSource,Class <? extends java.text.Annotation> annotationClass) throws Exception{
        List<Class <?>> toReturn = new ArrayList<>(); 
        List<Class <?>> allClasses = getAllClasses(packageSource);
        for (int i = 0; i < allClasses.size(); i++) {
            Class tempoClass = allClasses.get(i);
            if (tempoClass.isAnnotationPresent(annotationClass)) {
                toReturn.add(tempoClass);
            }
        }
        return toReturn;
    }
}
