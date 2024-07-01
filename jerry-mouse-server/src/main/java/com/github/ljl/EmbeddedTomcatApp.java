//package com.github.ljl;
//
//import org.apache.catalina.LifecycleException;
//import org.apache.catalina.startup.Tomcat;
//
///**
// * @program: jerry-mouse
// * @description: 仅测试用
// * @author: ljl
// * @create: 2024-06-28 15:08
// **/
//@Deprecated
//public class EmbeddedTomcatApp {
//    private static final String baseWarDir = "D:\\java-learning\\jerry-mouse\\src\\test\\webapps";
//
//    public static void main(String[] args) throws LifecycleException {
//        Tomcat tomcat = new Tomcat();
//
//        // Set the port number
//        tomcat.setPort(8080);
//
//        // Set the base directory (can be any temporary directory)
//        tomcat.setBaseDir("temp");
//
//        String appName = "webapp1";
//
//        String warFilePath = baseWarDir + "/springmvc-app1.war";
//        tomcat.addWebapp("/springmvc-app1", warFilePath);
//        tomcat.start();
//    }
//}
