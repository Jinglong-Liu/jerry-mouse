package com.github.ljl.jerrymouse.support.xml;

public interface IWebXmlManager {
    /**
     * 解析baseDir下面的webapps
     * @param baseDir
     */
    void parseWebappXml(String baseDir);

    /**
     * 加载JerryMouse本地的web.xml
     */
    void parseLocalWebXml();
}
