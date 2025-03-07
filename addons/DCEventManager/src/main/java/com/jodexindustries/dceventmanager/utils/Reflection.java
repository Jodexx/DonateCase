package com.jodexindustries.dceventmanager.utils;

import com.jodexindustries.donatecase.api.event.DCEvent;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Reflection {

    /**
     * Private helper method.
     *
     * @param connection the connection to the jar
     * @param pckgname   the package name to search for
     * @param classes    the current ArrayList of all classes. This method will simply
     *                   add new classes.
     * @throws IOException if it can't correctly read from the jar file.
     */
    private static void checkJarFile(JarURLConnection connection,
                                     String pckgname, ArrayList<Class<? extends DCEvent>> classes) throws IOException {
        final JarFile jarFile = connection.getJarFile();
        final Enumeration<JarEntry> entries = jarFile.entries();
        String name;

        for (JarEntry jarEntry; entries.hasMoreElements()
                && ((jarEntry = entries.nextElement()) != null); ) {
            name = jarEntry.getName();

            if (name.endsWith(".class")) {
                name = name.substring(0, name.length() - 6).replace('/', '.');

                if (name.startsWith(pckgname)) {
                    try {
                        Class<?> clazz = Class.forName(name);

                        if (DCEvent.class.isAssignableFrom(clazz) && clazz != DCEvent.class) {
                            classes.add(clazz.asSubclass(DCEvent.class));
                        }
                    } catch (Throwable ignored) {
                    }
                }
            }
        }
    }


    /**
     * Attempts to list all the classes in the specified package as determined
     * by the context class loader
     *
     * @param pckgname the package name to search
     * @return a list of classes that exist within that package
     */
    public static ArrayList<Class<? extends DCEvent>> getClassesForPackage(ClassLoader cld, String pckgname) throws ClassNotFoundException {
        final ArrayList<Class<? extends DCEvent>> classes = new ArrayList<>();

        try {
            if (cld == null)
                throw new ClassNotFoundException("Can't get class loader.");

            final Enumeration<URL> resources = cld.getResources(pckgname
                    .replace('.', '/'));
            URLConnection connection;

            for (URL url; resources.hasMoreElements()
                    && ((url = resources.nextElement()) != null); ) {
                try {
                    connection = url.openConnection();
                    if (connection instanceof JarURLConnection) {
                        checkJarFile((JarURLConnection) connection, pckgname,
                                classes);
                    } else
                        throw new ClassNotFoundException(pckgname + " ("
                                + url.getPath()
                                + ") does not appear to be a valid package");
                } catch (final IOException ex) {
                    throw new ClassNotFoundException(
                            "IOException was thrown when trying to get all resources for "
                                    + pckgname, ex);
                }
            }
        } catch (final NullPointerException ex) {
            throw new ClassNotFoundException(
                    pckgname
                            + " does not appear to be a valid package (Null pointer exception)",
                    ex);
        } catch (final IOException ex) {
            throw new ClassNotFoundException(
                    "IOException was thrown when trying to get all resources for "
                            + pckgname, ex);
        }

        return classes;
    }

    @Nullable
    public static <T> T getVar(DCEvent event, String methodName, Class<T> clazz) {
        T object = null;
        try {
            Method method = event.getClass().getMethod(methodName);
            method.setAccessible(true);
            Object result = method.invoke(event);
            if (clazz.isInstance(result)) {
                object = clazz.cast(result);
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
        }
        return object;
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
