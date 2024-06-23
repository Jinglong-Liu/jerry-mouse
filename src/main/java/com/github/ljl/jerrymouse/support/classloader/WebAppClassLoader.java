package com.github.ljl.jerrymouse.support.classloader;

import com.github.ljl.jerrymouse.servlet.manager.IClassLoader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-22 13:04
 **/

public class WebAppClassLoader extends URLClassLoader implements IClassLoader {

    private Path classPath;

    private Path[] libJars;

    public WebAppClassLoader(Path classPath, Path libPath) throws IOException {
        super(createUrls(classPath, libPath), ClassLoader.getSystemClassLoader());

        this.classPath = classPath.toAbsolutePath().normalize();
        if(libPath.toFile().exists()) {
            this.libJars = Files.list(libPath)
                    .filter(p -> p.toString().endsWith(".jar"))
                    .map(p -> p.toAbsolutePath().normalize())
                    .sorted()
                    .toArray(Path[]::new);
        }
    }

    static URL[] createUrls(Path classPath, Path libPath) throws IOException {
        List<URL> urls = new ArrayList<>();
        urls.add(toDirURL(classPath));

        //lib 可能不存在
        if(libPath.toFile().exists()) {
            Files.list(libPath).filter(p -> p.toString().endsWith(".jar")).sorted().forEach(p -> {
                urls.add(toJarURL(p));
            });
        }

        return urls.toArray(new URL[0]);
    }

    static URL toDirURL(Path p) {
        try {
            if (Files.isDirectory(p)) {
                String abs = toAbsPath(p);
                if (!abs.endsWith("/")) {
                    abs = abs + "/";
                }
                return URI.create("file://" + abs).toURL();
            }
            throw new IOException("Path is not a directory: " + p);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    //D:\xxx\src\test\webapps\servlet\WEB-INF\classes
    //D:\xxx\src\test\webapps\WEB-INF\classes

    static URL toJarURL(Path p) {
        try {
            if (Files.isRegularFile(p)) {
                String abs = toAbsPath(p);
                return URI.create("file://" + abs).toURL();
            }
            throw new IOException("Path is not a jar file: " + p);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static String toAbsPath(Path p) throws IOException {
        return p.toAbsolutePath().normalize().toString().replace('\\', '/');
    }

    @Override
    public Class loadClass(String className) {
        try {
            return super.loadClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
