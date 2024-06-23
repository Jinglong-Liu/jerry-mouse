package com.github.ljl.jerrymouse.support.classloader;

import com.github.ljl.jerrymouse.utils.JerryMouseFileUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-22 16:41
 **/

public class WebAppClassLoader extends ClassLoader implements IClassLoader {
    private Path classPath;
    private List<Path> jarPaths;

    public WebAppClassLoader(Path classPath, Path libPath) throws IOException {
        this.classPath = classPath;
        if(Objects.nonNull(libPath) && JerryMouseFileUtils.exists(libPath.toFile().getAbsolutePath())) {
            this.jarPaths = Files.walk(libPath)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".jar"))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Class<?> loadClass(String className) {
        try {
            return findClass(className);
        } catch (Exception e) {
            try {
                return super.loadClass(className);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        // Convert class name to file path
        String classFile = className.replace('.', '/') + ".class";
        Path classFilePath = classPath.resolve(classFile);

        if (Files.exists(classFilePath)) {
            try {
                byte[] classData = Files.readAllBytes(classFilePath);
                return defineClass(className, classData, 0, classData.length);
            } catch (IOException e) {
                throw new ClassNotFoundException("Could not load class " + className, e);
            }
        }

        // Try loading from JAR files
        for (Path jarPath : jarPaths) {
            try (JarFile jarFile = new JarFile(jarPath.toFile())) {
                JarEntry entry = jarFile.getJarEntry(classFile);
                if (entry != null) {
                    try (InputStream inputStream = jarFile.getInputStream(entry)) {
                        byte[] classData = toByteArray(inputStream);
                        return defineClass(className, classData, 0, classData.length);
                    }
                }
            } catch (IOException e) {
                // Continue to the next JAR file
            }
        }

        throw new ClassNotFoundException("Class " + className + " not found");
    }

    private byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }
}
