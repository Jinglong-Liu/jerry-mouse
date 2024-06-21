package com.github.ljl.jerrymouse.dto;

import com.github.ljl.jerrymouse.adaptor.JerryMouseRequestAdaptor;
import com.github.ljl.jerrymouse.exception.JerryMouseException;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-20 15:27
 **/

public class JerryMouseRequest extends JerryMouseRequestAdaptor {
    private static Logger logger = LoggerFactory.getLogger(JerryMouseRequest.class);

    private String method;

    private String url;

    @Getter
    private SocketChannel socketChannel;

    public JerryMouseRequest(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        parseSocketChannel();
    }

    private void parseSocketChannel() {
        StringBuilder requestBuffer = new StringBuilder(); // 缓存部分数据
        ByteBuffer buffer = ByteBuffer.allocate(1024); // 使用固定大小的缓冲区
        int bytesRead;
        try {
            while ((bytesRead = socketChannel.read(buffer)) > 0) {
                buffer.flip();
                byte[] data = new byte[buffer.remaining()];
                buffer.get(data);
                requestBuffer.append(new String(data));
                buffer.clear();

                // 检查是否读取到完整的HTTP请求行
                if (requestBuffer.toString().contains("\n")) {
                    // 获取第一行数据
                    String firstLineStr = requestBuffer.toString().split("\\n")[0];
                    String[] strings = firstLineStr.split(" ");
                    this.method = strings[0];
                    this.url = strings[1];

                    logger.info("[JerryMouse] method={}, url={}", method, url);
                    break; // 退出循环，因为我们已经读取到请求行
                }
            }
        } catch (IOException e) {
            logger.error("[JerryMouse] meet exception");
            throw new JerryMouseException(e);
        }


        if ("".equals(method)) {
            logger.info("[JerryMouse] No HTTP request line found, ignoring.");
            // 可以选择抛出异常或者返回空请求对象
        }

        if (bytesRead <= 0) {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }
}
