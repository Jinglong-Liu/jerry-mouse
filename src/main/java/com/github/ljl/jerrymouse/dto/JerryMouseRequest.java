package com.github.ljl.jerrymouse.dto;

import com.github.ljl.jerrymouse.dto.adaptor.JerryMouseRequestAdaptor;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class JerryMouseRequest extends JerryMouseRequestAdaptor {
    private static Logger logger = LoggerFactory.getLogger(JerryMouseRequest.class);

    private String method;

    private String url;
}
