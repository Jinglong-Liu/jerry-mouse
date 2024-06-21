package com.github.ljl.jerrymouse.dto;

import com.github.ljl.jerrymouse.adaptor.JerryMouseResponseAdaptor;
import com.github.ljl.jerrymouse.bootstrap.JerryMouseBootstrap;
import com.github.ljl.jerrymouse.exception.JerryMouseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-21 16:20
 **/
@Deprecated
public class JerryMouseResponseBio extends JerryMouseResponseAdaptor {
    private static Logger logger = LoggerFactory.getLogger(JerryMouseBootstrap.class);

    private final OutputStream outputStream;

    public JerryMouseResponseBio(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void write(byte[] bytes) {
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            logger.error("[JerryMouse] write outputStream meet exception", e);
            throw new JerryMouseException(e);
        }
    }
    public void write(String s) {
        write(s.getBytes());
    }
}
