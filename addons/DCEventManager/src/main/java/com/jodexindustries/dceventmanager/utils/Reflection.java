package com.jodexindustries.dceventmanager.utils;

import org.bukkit.event.Event;
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
     * @param connection
     *            the connection to the jar
     * @param pckgname
     *            the package name to search for
     * @param classes
     *            the current ArrayList of all classes. This method will simply
     *            add new classes.
     * @throws ClassNotFoundException
     *             if a file isn't loaded but still is in the jar file
     * @throws IOException
     *             if it can't correctly read from the jar file.
     */
    private static void checkJarFile(JarURLConnection connection,
                                     String pckgname, ArrayList<Class<? extends Event>> classes)
            throws ClassNotFoundException, IOException {
        final JarFile jarFile = connection.getJarFile();
        final Enumeration<JarEntry> entries = jarFile.entries();
        String name;

        for (JarEntry jarEntry; entries.hasMoreElements()
                && ((jarEntry = entries.nextElement()) != null);) {
            name = jarEntry.getName();

            if (name.contains(".class")) {
                name = name.substring(0, name.length() - 6).replace('/', '.');

                if (name.contains(pckgname)) {
                    Class<?> clazz = Class.forName(name);
                    if(Event.class.isAssignableFrom(clazz)) {
                        classes.add(clazz.asSubclass(Event.class));
                    }
                }
            }
        }
    }

    /**
     * Attempts to list all the classes in the specified package as determined
     * by the context class loader
     *
     * @param pckgname
     *            the package name to search
     * @return a list of classes that exist within that package
     */
    public static ArrayList<Class<? extends Event>> getClassesForPackage(ClassLoader cld, String pckgname) throws ClassNotFoundException {
        final ArrayList<Class<? extends Event>> classes = new ArrayList<>();

        try {
            if (cld == null)
                throw new ClassNotFoundException("Can't get class loader.");

            final Enumeration<URL> resources = cld.getResources(pckgname
                    .replace('.', '/'));
            URLConnection connection;

            for (URL url; resources.hasMoreElements()
                    && ((url = resources.nextElement()) != null);) {
                try {
                    connection = url.openConnection();
                    if (connection instanceof JarURLConnection) {
                        checkJarFile((JarURLConnection) connection, pckgname,
                                classes);
                    } else
                        throw new ClassNotFoundException(pckgname + " ("
                                + url.getPath()
                                + ") does not appear to be a valid package");
                } catch (final IOException ioex) {
                    throw new ClassNotFoundException(
                            "IOException was thrown when trying to get all resources for "
                                    + pckgname, ioex);
                }
            }
        } catch (final NullPointerException ex) {
            throw new ClassNotFoundException(
                    pckgname
                            + " does not appear to be a valid package (Null pointer exception)",
                    ex);
        } catch (final IOException ioex) {
            throw new ClassNotFoundException(
                    "IOException was thrown when trying to get all resources for "
                            + pckgname, ioex);
        }

        return classes;
    }

    public static boolean hasVar(Event event, String methodName) {
        try {
            event.getClass().getMethod(methodName);
        } catch (NoSuchMethodException e) {
            return false;
        }
        return true;
    }

    @Nullable
    public static <T> T getVar(Event event, String methodName, Class<T> clazz) {
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

    public static Object invokeMethodChain(Object event, String method) {
        try {
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
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
