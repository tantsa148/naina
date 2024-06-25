package org.myspringframework.util;

import org.myspringframework.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ReflectionUtils {

    public static Object invokeMethodWithRequestParams(String className, String methodName, HttpServletRequest request) throws Exception {
        // Charger la classe à partir de son nom
        Class<?> clazz = Class.forName(className);
        Object object = clazz.getDeclaredConstructor().newInstance();
        Method method = clazz.getDeclaredMethod(methodName);
        method.setAccessible(true);
        // method.getParameters()
       
        // Obtenir la méthode spécifiée
        if (method == null) {
            throw new NoSuchMethodException("No suitable method found");
        }
        // Obtenir les paramètres de la méthode
        /*Object[] params = getParameters(request, method);
        // Appeler la méthode sur l'objet
        if(params.length!=0){
            return params[0]; 
            // Method method = ReflectionUtils.getMethodWithRequestParams(clazz, methodName);
            // method.invoke(object, params);
        }else{
            */
    return method.invoke(clazz.newInstance());
        // }
    }

    public static Method getMethodWithRequestParams(Class<?> clazz, String methodName) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName) && hasRequestParamAnnotation(method)) {
                return method;
            }
        }
        return null;
    }

    public static boolean hasRequestParamAnnotation(Method method) {
        for (Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(RequestParam.class)) {
                return true;
            }
        }
        return false;
    }

    public static Object[] getParameters(HttpServletRequest request, Method method) {
        Parameter[] parameters = method.getParameters();
        Object[] params = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
            if (requestParam != null) {
                String paramName = requestParam.value();
                String paramValue = request.getParameter(paramName);
                params[i] = convertType(paramValue, parameters[i].getType());
            }       
        }

        return params;
    }

    public static Object convertType(String value, Class<?> targetType) {
        if (value == null) {
            return null; // Handle null value if necessary
        }

        if (targetType == String.class) {
            return value;
        } else if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        } else if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(value);
        } else if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(value);
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        }
        // Ajoutez d'autres types si nécessaire
        return value;
    }
}
