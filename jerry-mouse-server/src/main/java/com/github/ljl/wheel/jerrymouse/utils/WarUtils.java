package com.github.ljl.wheel.jerrymouse.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 17:01
 **/

public class WarUtils {
    private static final Logger logger = LoggerFactory.getLogger(WarUtils.class);

    /**
     * @param baseDir 存放 .war包的目录
     * @return appName set(.war的前缀构成的集合)
     */
    public static Set<String> extract(String baseDir) {
        Map<String, String> appNames = new ConcurrentHashMap<>();
        logger.debug("Start extract baseDirStr={}", baseDir);

        // check exist
        File baseDirFile = new File(baseDir);
        if (!baseDirFile.exists()) {
            logger.error("Base Dir {} not found!", baseDir);
            return Collections.emptySet();
        }

        // list war
        File[] appWars = baseDirFile.listFiles();
        if (Objects.isNull(appWars)) {
            logger.error("Found no webapp wars");
            return Collections.emptySet();
        }

        // filter
        final List<File> warList = Arrays.stream(appWars)
                .filter(appWar -> appWar.getName().endsWith(".war"))
                .collect(Collectors.toList());
        logger.info("Found {} war packages", warList.size());

        // handle each war package
        CountDownLatch latch = new CountDownLatch(warList.size());
        warList.forEach(warFile -> {
            ThreadPoolUtils.execute(()-> {
                String fileName = warFile.getName();
                logger.debug("Start extract war={}", fileName);
                String appName = fileName.substring(0, fileName.lastIndexOf(".war"));
                appNames.put(appName, appName);
                extractWarFile(baseDir, warFile);
                latch.countDown();
            });
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.info("Finish extract {} war package", warList.size());
        return appNames.keySet();
    }

    /**
     * 处理 war 文件
     *
     * @param baseDirStr baseDir
     * @param warFile    war 文件
     */
    private static void extractWarFile(String baseDirStr,
                                       File warFile) {
        //1. 删除历史的 war 解压包
        String warPackageName = FileUtils.getFileName(warFile.getName());
        String fullWarPackagePath = FileUtils.buildFullPath(baseDirStr, warPackageName);
        FileUtils.deleteFileRecursive(fullWarPackagePath);

        //2. 解压文件
        try {
            extractWar(warFile.getAbsolutePath(), fullWarPackagePath);
        } catch (IOException e) {
            logger.error("ExtractWar failed, warPackageName={}", warFile.getAbsoluteFile(), e);
            e.printStackTrace();
        }
    }
    private static void extractWar(String warFilePath, String destinationDirectory) throws IOException {
        File destinationDir = new File(destinationDirectory);
        if (!destinationDir.exists()) {
            destinationDir.mkdirs();
        }

        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(Paths.get(warFilePath)))) {
            ZipEntry entry = zipInputStream.getNextEntry();
            while (entry != null) {
                String filePath = destinationDirectory + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    extractFile(zipInputStream, filePath);
                } else {
                    File dir = new File(filePath);
                    dir.mkdir();
                }
                zipInputStream.closeEntry();
                entry = zipInputStream.getNextEntry();
            }
        }
    }

    private static void extractFile(ZipInputStream zipInputStream, String filePath) throws IOException {
        FileUtils.createFile(filePath);
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = zipInputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }
    }
}
