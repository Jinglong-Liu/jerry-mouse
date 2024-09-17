package com.github.ljl.wheel.jerrymouse.support.classloader;

import com.github.ljl.wheel.jerrymouse.utils.FileUtils;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
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
 * @create: 2024-09-14 16:52
 **/

public class WebApplicationClassLoader extends URLClassLoader {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ClassLoader parent;
    private String baseDir;
    private final Path classPath;
    private final List<Path> jarPaths;


    /**
     *
     * @param urls: urls[0] is baseDir
     * @param parent: classLoader
     */
    @SneakyThrows
    public WebApplicationClassLoader(@NotNull URL[] urls, @NotNull ClassLoader parent) {
        super(urls, parent);
        this.parent = parent;
        this.baseDir = urls[0].getPath();
        // windows

        if (baseDir.startsWith("/") && System.getProperty("os.name").toLowerCase().contains("win")) {
            baseDir = baseDir.substring(1);
        }
        this.classPath =  Paths.get(FileUtils.buildFullPath(baseDir,  "/WEB-INF/classes/"));
        Path libPath = Paths.get(FileUtils.buildFullPath(baseDir,  "/WEB-INF/lib/"));
        if(FileUtils.exists(libPath.toFile().getAbsolutePath())) {
            this.jarPaths = Files.walk(libPath)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".jar"))
                    .collect(Collectors.toList());
        } else {
            jarPaths = Collections.emptyList();
        }

    }

    @Override
    public Class<?> loadClass(String className) {
        try {
            Class<?> clazz = parent.loadClass(className);
            if (Objects.isNull(clazz)) {
                throw new ClassNotFoundException();
            } else {
                return clazz;
            }
        } catch (ClassNotFoundException e) {
            try {
                return findClass(className);
            } catch (ClassNotFoundException ignored) {
            }
        }
        return null;
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        try {
            Class<?> clazz = parent.loadClass(className);
            if (Objects.isNull(clazz)) {
                throw new ClassNotFoundException();
            } else {
                return clazz;
            }
        } catch (ClassNotFoundException ignored) {

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
        logger.warn("Class " + className + "not found");
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

    private List<URL> findURLFromJarPath(String name) {
        List<URL> resources = new ArrayList<>();
        try {
            for (Path jarPath : jarPaths) {
                URL jarUrl = jarPath.toUri().toURL();
                try (JarFile jarFile = ((JarURLConnection) new URL("jar:" + jarUrl + "!/").openConnection()).getJarFile()) {
                    ZipEntry entry = jarFile.getEntry(name);
                    if (entry != null) {
                        /**
                         * 形如
                         * jar:file:/D:/java-learning/jerry-mouse/src/test/webapps/springmvc-app1/WEB-INF/lib/spring-web-5.3.3.jar!/org/springframework/web/context/ContextLoader.properties
                         */
                        resources.add(new URL("jar:" + jarUrl + "!/" + name));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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
            Path resourcePath = Paths.get(FileUtils.buildFullPath(classPath.toFile().getAbsolutePath(), name));
            if (Files.exists(resourcePath)) {
                return resourcePath.toUri().toURL();
            }

            // 2.查找baseDir
            Path rootPath = Paths.get(FileUtils.buildFullPath(baseDir, name));
            if (Files.exists(rootPath)) {
                return rootPath.toUri().toURL();
            }

            // 3. 查找jar
            List<URL> jarURL = findURLFromJarPath(name);
            if (!jarURL.isEmpty()) {
                return jarURL.get(0);
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
         * including springmvc project:
         * \META-INF\spring.schemas
         * \META-INF\spring.handlers
         */

        resources.addAll(findURLFromJarPath(name));

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
    private List<URL> getClassesDirResources(String name) throws IOException {
        List<URL> resources = new ArrayList<>();
        File directory = new File(this.classPath.toFile().getAbsolutePath(), name);
        if (directory.exists()) {
            logger.debug(directory.getAbsolutePath() + " exist!!!");
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
}
