package com.github.ljl.jerrymouse.classloader;

import com.github.ljl.jerrymouse.utils.JerryMouseFileUtils;
import com.github.ljl.jerrymouse.utils.JerryMouseResourceUtils;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-22 16:41
 **/

public class WebAppClassLoader extends URLClassLoader {
    private String baseDir;
    private Path classPath;
    private List<Path> jarPaths;
    private ClassLoader parent;

    public WebAppClassLoader(URL[] urls, ClassLoader parent) throws IOException {
        super(urls, parent);
        this.parent = parent;
        this.baseDir = urls[0].getPath();
        this.classPath =  Paths.get(JerryMouseResourceUtils.buildFullPath(baseDir,  "/WEB-INF/classes/"));
        Path libPath = Paths.get((JerryMouseResourceUtils.buildFullPath(baseDir, "/WEB-INF/lib/")));
        if(Objects.nonNull(libPath) && JerryMouseFileUtils.exists(libPath.toFile().getAbsolutePath())) {
            this.jarPaths = Files.walk(libPath)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".jar"))
                    .collect(Collectors.toList());
        }
    }

    private List<URL> getClassesDirResources(String name) throws IOException {
        List<URL> resources = new ArrayList<>();
        File directory = new File(this.classPath.toFile().getAbsolutePath(), name);
        if (directory.exists()) {
            System.out.println(directory.getAbsolutePath() + " exist!!!");
            if (directory.isDirectory()) {
                // 查找指定目录及其子目录中的资源
                Files.walk(directory.toPath()).forEach(path -> {
                    File file = path.toFile();
                    if (file.exists()) {
                        try {
                            resources.add(file.toURI().toURL());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        return resources;
    }


    @Override
    public URL getResource(String name) {
        URL url = super.getResource(name);
        if (Objects.nonNull(url)) {
            return url;
        }
        try {
            // 1. 查找 classPath 目录
            Path resourcePath = classPath.resolve(name);
            if (Files.exists(resourcePath)) {
                return resourcePath.toUri().toURL();
            }

            // 2. 查找 jarPaths 列表中的每个 JAR 文件
            for (Path jarPath : jarPaths) {
                URL jarUrl = jarPath.toUri().toURL();
                try (JarFile jarFile = ((JarURLConnection) new URL("jar:" + jarUrl + "!/").openConnection()).getJarFile()) {
                    ZipEntry entry = jarFile.getEntry(name);
                    if (entry != null) {
                        /**
                         * 形如
                         * jar:file:/D:/java-learning/jerry-mouse/src/test/webapps/springmvc-app1/WEB-INF/lib/spring-web-5.3.3.jar!/org/springframework/web/context/ContextLoader.properties
                         */
                        return new URL("jar:" + jarUrl + "!/" + name);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 如果没有找到，返回 null
        // 添加父类加载器的资源
        System.out.println("resource not found!!! " + name);
        return null;
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        // class
        List<URL> resources = getClassesDirResources(name);

        resources.addAll(Collections.list(super.getResources(name)));


        // add jar file: very important!!!
        /**
         * or else:
         * D:\java-learning\jerry-mouse\src\test\webapps\springmvc-app1\META-INF\spring.schemas NOT exist!!!
         * D:\java-learning\jerry-mouse\src\test\webapps\springmvc-app1\META-INF\spring.handlers NOT exist!!!
         * org.springframework.beans.factory.parsing.BeanDefinitionParsingException: Configuration problem: Unable to locate Spring NamespaceHandler for XML schema namespace [http://www.springframework.org/schema/context]
         * Offending resource: ServletContext resource [/WEB-INF/applicationContext.xml]
         */
        for (Path jarPath : jarPaths) {
            try (JarFile jarFile = new JarFile(jarPath.toFile())) {
                JarEntry entry = jarFile.getJarEntry(name);
                if (entry != null) {
                    /**
                     * 形如
                     * jar:file:D:\java-learning\jerry-mouse\src\test\webapps\springmvc-app1\WEB-INF\lib\spring-beans-5.3.3.jar!/META-INF/spring.factories
                     */
                    resources.add(new URL("jar:file:" + jarFile.getName() + "!/" + name));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        // baseDir
        File directory = new File(this.baseDir, name);
        if (directory.exists()) {
            if (directory.isDirectory()) {
                resources.addAll(findResourcesInDirectory(directory));
            } else if (directory.isFile()){
                resources.add(directory.toURI().toURL());
            }
        } else {
            System.out.println(directory.getAbsolutePath() + " NOT exist!!!");
        }
        return Collections.enumeration(resources);
    }

    private List<URL> findResourcesInDirectory(File dir) throws IOException {
        List<URL> resources = new ArrayList<>();
        if (Objects.isNull(dir)) {
            return resources;
        }

        if (dir.isDirectory()) {
            Files.walk(dir.toPath())
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            resources.add(path.toUri().toURL());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    });
        }
        return resources;
    }

    @Override
    public Class<?> loadClass(String className) {
        try {
            return super.loadClass(className);
        } catch (ClassNotFoundException e) {
            try {
                return findClass(className);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return super.getResourceAsStream(name);
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        try {
            Class clazz = parent.loadClass(className);
            if (Objects.isNull(clazz)) {
                throw new ClassNotFoundException();
            } else {
                return clazz;
            }
        } catch (ClassNotFoundException ee) {

        }
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
                System.out.println("load jar file err" + jarPath.getFileName());
                // Continue to the next JAR file
            }
        }

        // throw new ClassNotFoundException("Class " + className + " not found");
        System.out.println("Class " + className + "not found");
        return null;
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
