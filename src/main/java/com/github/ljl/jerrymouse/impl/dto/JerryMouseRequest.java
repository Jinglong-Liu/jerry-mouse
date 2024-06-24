package com.github.ljl.jerrymouse.impl.dto;

import com.github.ljl.jerrymouse.impl.dto.adaptor.JerryMouseRequestAdaptor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-20 15:27
 **/

@Data
public class JerryMouseRequest extends JerryMouseRequestAdaptor {
    private static Logger logger = LoggerFactory.getLogger(JerryMouseRequest.class);

    public JerryMouseRequest(String method, String url) {
        this.method = method;
        this.url = url;
    }
    private String method;

    private String url;
}
