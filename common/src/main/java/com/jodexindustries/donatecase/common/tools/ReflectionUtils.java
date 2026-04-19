package com.jodexindustries.donatecase.common.tools;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ReflectionUtils {

    /**
     * Attempts to list all the classes in the specified package as determined
     * by the context class loader
     *
     * @param pkg the package name to search
     * @return a list of classes that exist within that package
     */
    public static List<Class<?>> getClasses(ClassLoader cld, String pkg) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        String path = pkg.replace('.', '/');

        try {
            Enumeration<URL> resources = cld.getResources(path);

            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();

                String file = url.getFile();

                if (!file.contains("!")) continue;

                String jarPath = file.substring(0, file.indexOf("!")).replace("file:", "");

                try (JarFile jar = new JarFile(jarPath)) {
                    Enumeration<JarEntry> entries = jar.entries();

                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();

                        if (!name.startsWith(path) || !name.endsWith(".class") || name.indexOf('$') != -1)
                            continue;

                        String className = name.replace('/', '.').substring(0, name.length() - 6);

                        try {
                            classes.add(Class.forName(className, false, cld));
                        } catch (Throwable ignored) {}
                    }
                }
            }

        } catch (IOException e) {
            throw new ClassNotFoundException("Failed to scan package " + pkg, e);
        }

        return classes;
    }

    public static Object invokeMethodChain(Object event, String method) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String[] methods = method.split("#");
        for (String methodName : methods) {
            // Check if the method contains arguments
            String methodNameWithoutArgs = methodName.split("\\(")[0];
            String argsString = methodName.contains("(") ? methodName.substring(methodName.indexOf('(') + 1, methodName.indexOf(')')) : "";

            // Parse arguments
            String[] argStrings = argsString.isEmpty() ? new String[0] : argsString.split(",");
            Object[] args = new Object[argStrings.length];
            Class<?>[] argTypes = new Class<?>[argStrings.length];

            for (int i = 0; i < argStrings.length; i++) {
                String arg = argStrings[i].trim();
                if (arg.equals("true") || arg.equals("false")) {
                    args[i] = Boolean.parseBoolean(arg);
                    argTypes[i] = boolean.class;
                } else {
                    try {
                        args[i] = Integer.parseInt(arg);
                        argTypes[i] = int.class;
                    } catch (NumberFormatException e) {
                        args[i] = arg; // Assume it's a String if it's not a boolean or int
                        argTypes[i] = String.class;
                    }
                }
            }

            // Find and invoke the method
            Method m;
            if (args.length > 0) {
                m = event.getClass().getMethod(methodNameWithoutArgs, argTypes);
            } else {
                m = event.getClass().getMethod(methodNameWithoutArgs);
            }
            m.setAccessible(true);
            event = m.invoke(event, args);
        }
        return event;
    }

}
