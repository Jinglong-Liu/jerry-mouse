package com.github.ljl.jerrymouse.support.context;

import com.github.ljl.jerrymouse.annotation.Singleton;
import com.github.ljl.jerrymouse.impl.dto.IRequest;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @since 0.7.1
 * @create: 2024-06-24 17:48
 **/

@Singleton
public class JerryMouseContextManager implements IContextManager {

    private Map<String, ServletContext> contextMap = new HashMap<>();

    private static JerryMouseContextManager instance;

    private JerryMouseContextManager() {

    }
    public static JerryMouseContextManager get() {
        if(Objects.isNull(instance)) {
            synchronized (JerryMouseContextManager.class) {
                if(Objects.isNull(instance)) {
                    instance = new JerryMouseContextManager();
                }
            }
        }
        return instance;
    }

    @Override
    public ServletContext getServletContext(IRequest request) {
        String url = request.getUrl();
        String prefix = getPrefix(url);
        ServletContext context = getServletContext(prefix);
        // 匹配不上，返回本地JerryMouse自身的context
        if (Objects.isNull(context)) {
            return getLocalContext();
        }
        return context;
    }

    private ServletContext getLocalContext() {
        return getServletContext("");
    }
    private String getPrefix(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }

        // 找到第二个斜杠的位置
        int firstSlashIndex = url.indexOf('/');
        int secondSlashIndex = url.indexOf('/', firstSlashIndex + 1);

        // 如果找不到第二个斜杠，返回所有内容
        if (secondSlashIndex == -1) {
            return url;
        }

        // 返回从第一个斜杠到第二个斜杠之间的部分
        return url.substring(0, secondSlashIndex);
    }


    @Override
    public ServletContext getServletContext(String name) {
        return contextMap.get(name);
    }

    @Override
    public void registerServletContext(String name, ServletContext context) {
        contextMap.put(name, context);
    }
}
