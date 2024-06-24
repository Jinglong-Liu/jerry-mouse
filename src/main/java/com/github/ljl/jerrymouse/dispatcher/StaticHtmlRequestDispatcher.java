package com.github.ljl.jerrymouse.dispatcher;

import com.github.ljl.jerrymouse.bootstrap.JerryMouseBootstrap;
import com.github.ljl.jerrymouse.impl.dto.IRequest;
import com.github.ljl.jerrymouse.impl.dto.IResponse;
import com.github.ljl.jerrymouse.exception.JerryMouseException;
import com.github.ljl.jerrymouse.utils.JerryMouseFileUtils;
import com.github.ljl.jerrymouse.utils.JerryMouseHttpUtils;
import com.github.ljl.jerrymouse.utils.JerryMouseResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-20 19:15
 **/

public class StaticHtmlRequestDispatcher implements IRequestDispatcher{
    private static Logger logger = LoggerFactory.getLogger(StaticHtmlRequestDispatcher.class);
    @Override
    public void dispatch(RequestDispatcherContext context) {
        final IRequest request = context.getRequest();
        final IResponse response = context.getResponse();

        String absolutePath = JerryMouseResourceUtils.buildFullPath(
                JerryMouseResourceUtils.getClassRootResource(JerryMouseBootstrap.class), request.getUrl());
        try {
            String content = JerryMouseFileUtils.getFileContent(absolutePath);
            logger.info("[JerryMouse] static html path: {}, content={}", absolutePath, content);
            String html = JerryMouseHttpUtils.http200Resp(content);
            response.write(html);
        } catch (JerryMouseException e) {
            response.write(JerryMouseHttpUtils.http404Resp());
        }
    }
}
