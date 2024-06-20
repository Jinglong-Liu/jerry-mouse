# jerry-mouse
A simplified Tomcat

## step 1: socket 监听

需求:
1、启动一个socket，监听8080端口，返回一个简易符合http协议的信息，在页面上解析结果为
`Hello JerryMouse!

2、提供关闭socket服务的接口stop()

3、测试socket的开启与关闭
`
```java
    private static String httpResp(String rawText) {
        String format = "HTTP/1.1 200 OK\r\n" +
        "Content-Type: text/plain\r\n" +
        "\r\n" +
        "%s";
        return String.format(format, rawText);
    }
    while(true) {
        Socket socket = serverSocket.accept();
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(httpResp("Hello JerryMouse!").getBytes());
        socket.close();
    }
```

注意使用异步方式启动，不要阻塞主线程

测试代码:Main.java

至此，很好理解。

git log: [jerry-mouse] simple socket

