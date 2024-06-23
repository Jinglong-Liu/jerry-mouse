package com.github.ljl.jerrymouse.utils;

import com.github.ljl.jerrymouse.bootstrap.JerryMouseBootstrap;
import com.github.ljl.jerrymouse.exception.JerryMouseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @since 0.5.0
 * @create: 2024-06-20 15:56
 **/

public class JerryMouseFileUtils {
    private static Logger logger = LoggerFactory.getLogger(JerryMouseBootstrap.class);

    public static String getFileContent(String absolutePath) {
        try {
            // 读取文件内容并返回为字符串
            return new String(Files.readAllBytes(Paths.get(absolutePath)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("read file from path: {} error", absolutePath);
            throw new JerryMouseException(e);
        }
    }
    /**
     * 获取文件名称
     *
     * @param path 完整路径
     * @return 名称
     * @since 0.5.0
     */
    public static String getFileName(final String path) {
        if (StringUtil.isEmptyTrim(path)) {
            return StringUtil.EMPTY;
        }

        File file = new File(path);
        String name = file.getName();

        return name.substring(0, name.lastIndexOf('.'));
    }
    /**
     * 递归删除文件（包含子文件+文件夹）
     *
     * @param file 文件信息
     * @since 0.5.0
     */
    public static void deleteFileRecursive(final File file) {
        if(Objects.isNull(file)) {
            throw new JerryMouseException("file is null");
        }
        deleteFileRecursive(file.getAbsolutePath());
    }
    /**
     * 递归删除文件（包含子文件+文件夹）
     *
     * @param filePath 文件信息
     * @since 0.5.0
     */
    public static void deleteFileRecursive(final String filePath) {
        if(StringUtil.isEmpty(filePath)) {
            throw new JerryMouseException("file is empty, path = " + filePath);
        }
        File dir = new File(filePath);

        File[] list = dir.listFiles();  //无法做到list多层文件夹数据
        if (list != null) {
            for (File temp : list) {     //先去递归删除子文件夹及子文件
                deleteFileRecursive(temp.getAbsolutePath());   //注意这里是递归调用
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
     * @since 0.5.0
     */
    public static boolean createFile(final String filePath) throws IOException {
        if (StringUtil.isEmpty(filePath)) {
            return false;
        }

        if (JerryMouseFileUtils.exists(filePath)) {
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
        if (StringUtil.isEmpty(filePath)) {
            return false;
        }

        Path path = Paths.get(filePath);
        return Files.exists(path, options);
    }
}
