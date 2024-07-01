package com.github.ljl.jerrymouse.support.war;

import com.github.ljl.jerrymouse.exception.JerryMouseException;
import com.github.ljl.jerrymouse.utils.JerryMouseFileUtils;
import com.github.ljl.jerrymouse.utils.JerryMouseResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @since 0.5.0
 * @create: 2024-06-22 13:36
 **/

public class WarExtractor implements IWarExtractor {

    Logger logger = LoggerFactory.getLogger(WarExtractor.class);
    @Override
    public void extract(String baseDirStr) {
        logger.info("[JerryMouse] start extract baseDirStr={}", baseDirStr);

        //1. check
//        Path baseDir = Paths.get(baseDirStr);
        File baseDirFile = new File(baseDirStr);
        if (!baseDirFile.exists()) {
            logger.error("[JerryMouse] base dir not found!");
            throw new JerryMouseException("baseDir not found!");
        }

        //2. list war
        File[] files = baseDirFile.listFiles();
        if (Objects.nonNull(files)) {
            for (File file : files) {
                String fileName = file.getName();
                if (fileName.endsWith(".war")) {
                    logger.error("[JerryMouse] start extract war={}", fileName);
                    handleWarFile(baseDirStr, file);
                }
            }
        }

        // handle war package
        logger.info("[JerryMouse] end extract baseDir={}", baseDirStr);
    }

    /**
     * 处理 war 文件
     *
     * @param baseDirStr 基础
     * @param warFile    war 文件
     */
    private void handleWarFile(String baseDirStr,
                               File warFile) {
        //1. 删除历史的 war 解压包
        String warPackageName = JerryMouseFileUtils.getFileName(warFile.getName());
        String fullWarPackagePath = JerryMouseResourceUtils.buildFullPath(baseDirStr, warPackageName);
        JerryMouseFileUtils.deleteFileRecursive(fullWarPackagePath);

        //2. 解压文件
        try {
            extractWar(warFile.getAbsolutePath(), fullWarPackagePath);
        } catch (IOException e) {
            logger.error("[JerryMouse] handleWarFile failed, warPackageName={}", warFile.getAbsoluteFile(), e);
            throw new JerryMouseException(e);
        }
    }
    public static void extractWar(String warFilePath, String destinationDirectory) throws IOException {
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
        JerryMouseFileUtils.createFile(filePath);

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = zipInputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }
    }
}
