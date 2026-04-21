package com.jodexindustries.donatecase.common.tools;

import com.jodexindustries.donatecase.api.DCAPI;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ReflectionUtils {

    private static final int maxMajor;

    static {
        int mc = DCAPI.getInstance().getPlatform().getPlatformVersion();

        if (mc <= 16) {
            maxMajor = 60; // Java 16
        } else if (mc <= 18) {
            maxMajor = 61; // Java 17
        } else if (mc <= 20) {
            maxMajor = 64; // Java 20
        } else {
            String[] versionElements = System.getProperty("java.version").split("\\.");
            int discard = Integer.parseInt(versionElements[0]);
            int version;
            if (discard == 1) {
                version = Integer.parseInt(versionElements[1]);
            } else {
                version = discard;
            }
            maxMajor = version + 44;
        }
    }

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

                        // fix paper plugin remapper
                        try (InputStream is = jar.getInputStream(entry)) {
                            int major = getClassMajorVersion(is);

                            if (major > maxMajor) {
                                System.out.println("Major: " + major + " Max: " + maxMajor);
                                continue;
                            }
                        } catch (Throwable ignored) {
                            continue;
                        }

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

    public static int getClassMajorVersion(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);

        if (dis.readInt() != 0xCAFEBABE) {
            throw new IOException("Invalid class");
        }

        dis.readUnsignedShort(); // minor
        return dis.readUnsignedShort(); // major
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
