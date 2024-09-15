package com.github.ljl.wheel.jerrymouse.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 14:05
 **/

public class FileUtils {
    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

    private static final String os = System.getProperty("os.name").toLowerCase();

    public static String buildFullPath(String prefix, String path) {
        // 检查前缀是否已以斜杠结尾，如果是，则直接拼接路径；如果不是，则在前缀后添加斜杠再拼接路径
        if(path.startsWith("/")) {
            path = path.substring(1);
        }
        String result = path;
        if(prefix.endsWith(File.separator)) {
            result = prefix + path;
        } else {
            result = prefix + File.separator + path;
        }
        // window系统
        if(os.contains("win") && (result.startsWith("\\") || result.startsWith("/"))) {
            result = result.substring(1);
        }
        return result;
    }

    /**
     * 获取文件名称
     *
     * @param path 完整路径
     * @return 名称
     */
    public static String getFileName(final String path) {
        if (StringUtils.isEmptyTrim(path)) {
            return StringUtils.EMPTY;
        }

        File file = new File(path);
        String name = file.getName();

        return name.substring(0, name.lastIndexOf('.'));
    }

    /**
     * 递归删除文件（包含子文件+文件夹）
     *
     * @param filePath 文件信息
     */
    public static void deleteFileRecursive(final String filePath) {
        if(StringUtils.isEmpty(filePath)) {
            logger.error("FilePath is empty, path = " + filePath);
        }
        File dir = new File(filePath);

        if (!dir.exists()) {
            logger.info("Dir not exist, not required to delete. Path = " + filePath);
            return;
        }
        File[] subList = dir.listFiles();  // 当前目录下
        if (subList != null) {
            for (File sub : subList) {     // 先去递归删除子文件夹及子文件
                deleteFileRecursive(sub.getAbsolutePath());   // 递归调用
            }
        }

        //再删除自己本身的文件夹
        dir.delete();
    }

    /**
     * 创建文件
     * （1）文件路径为空，则直接返回 false
     * （2）如果文件已经存在，则返回 true
     * （3）如果文件不存在，则创建文件夹，然后创建文件。
     * 3.1 如果父类文件夹创建失败，则直接返回 false.
     *
     * @param filePath 文件路径
     * @return 是否成功
     * @throws IOException 运行时异常，如果创建文件异常。包括的异常为 {@link IOException} 文件异常.
     */
    public static boolean createFile(final String filePath) throws IOException {
        if (StringUtils.isEmpty(filePath)) {
            return false;
        }

        if (exists(filePath)) {
            return true;
        }

        File file = new File(filePath);

        // 父类文件夹的处理
        File dir = file.getParentFile();
        if (dir != null && notExists(dir)) {
            boolean mkdirResult = dir.mkdirs();
            if (!mkdirResult) {
                return false;
            }
        }
        // 创建文件
        return file.createNewFile();
    }
    public static boolean notExists(final File file) {
        if(Objects.isNull(file)) {
            logger.error("file is null");
            return false;
        }
        return !file.exists();
    }
    /**
     * 文件是否存在
     *
     * @param filePath 文件路径
     * @param options  连接选项
     * @return 是否存在
     * @since 0.1.24
     */
    public static boolean exists(final String filePath, LinkOption... options) {
        if (StringUtils.isEmpty(filePath)) {
            return false;
        }

        Path path = Paths.get(filePath);
        return Files.exists(path, options);
    }
}
