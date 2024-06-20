package com.github.ljl.jerrymouse.dto;

import com.github.ljl.jerrymouse.exception.JerryMouseException;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-20 15:27
 **/

public class JerryMouseRequest {
    private static Logger logger = LoggerFactory.getLogger(JerryMouseRequest.class);

    private String method;

    @Getter
    private String url;

    private InputStream inputStream;

    public JerryMouseRequest(InputStream inputStream) {
        this.inputStream = inputStream;
        this.parseInputStream();
    }
    private void parseInputStream() {

        byte[] buffer = new byte[1024]; // 使用固定大小的缓冲区
        int bytesRead = 0;

        // 注意不要使用inputStream.available()
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) { // 循环读取数据直到EOF
                String inputStr = new String(buffer, 0, bytesRead);

                // 检查是否读取到完整的HTTP请求行
                if (inputStr.contains("\n")) {
                    // 获取第一行数据
                    String firstLineStr = inputStr.split("\\n")[0];
                    String[] strings = firstLineStr.split(" ");
                    this.method = strings[0];
                    this.url = strings[1];

                    logger.info("[JerryMouse] method={}, url={}", method, url);
                    break; // 退出循环，因为我们已经读取到请求行
                }
            }

            if ("".equals(method)) {
                logger.info("[JerryMouse] No HTTP request line found, ignoring.");
                // 可以选择抛出异常或者返回空请求对象
            }
        } catch (IOException e) {
            logger.error("[JerryMouse] readFromStream meet ex", e);
            throw new JerryMouseException(e);
        }
    }
}
