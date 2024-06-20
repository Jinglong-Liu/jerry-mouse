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
class JerryMouseBootStrap {
    private static String httpResp(String rawText) {
        String format = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                "%s";
        return String.format(format, rawText);
    }
    // 异步
    public void start() {
        threadPool.execute(() -> {
            startService();
        });
    }
    private void startService() {
        // ...
        while(true)
        {
            Socket socket = serverSocket.accept();
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(httpResp("Hello JerryMouse!").getBytes());
            socket.close();
        }
        // ...
    }
}
```

注意使用异步方式启动，不要阻塞主线程

测试代码:Main.java

至此，很好理解。

git log: [jerry-mouse] simple socket

## step 2: request, response 封装 与静态html返回

需求
1、将InputStream，OutputStream封装成Request和Response
2、Request对象解析请求，获得url和method
3、将对应路径下的 /index.html返回

```java
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

class JerryMouseResponse {
    public JerryMouseResponse(OutputStream outputStream) {
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
}
public class JerryMouseBootstrap {
    //...
    public void start() {
        threadPool.execute(() -> {
            startService();
        });
    }

    private void startService() {
        if(runningFlag) {
            logger.warn("[Jerry-mouse] server is already start!");
            return;
        }
        logger.info("[Jerry-mouse] start listen on port {}", port);
        logger.info("[Jerry-mouse] visit url http://{}:{}/index.html", LOCAL_HOST, port);
        try {
            this.serverSocket = new ServerSocket(port);
            runningFlag = true;
            while(runningFlag && !serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                JerryMouseRequest request = new JerryMouseRequest(socket.getInputStream());
                JerryMouseResponse response = new JerryMouseResponse(socket.getOutputStream());
                // response.write(httpResp("Hello JerryMouse!").getBytes());
                String staticHtmlPath = request.getUrl(); // null
                // 展示静态html文件
                if(Objects.nonNull(staticHtmlPath) && staticHtmlPath.endsWith(".html")) {
                    String absolutePath = JerryMouseResourceUtils
                            .buildFullPath(JerryMouseResourceUtils
                                            .getClassRootResource(JerryMouseBootstrap.class)
                                    , staticHtmlPath);
                    String content = JerryMouseFileUtils.getFileContent(absolutePath);
                    logger.info("[JerryMouse] static html path: {}, content={}", absolutePath, content);
                    String html = JerryMouseHttpUtils.http200Resp(content);
                    response.write(html);
                }
                else {
                    String html = JerryMouseHttpUtils.http404Resp();
                    response.write(html);
                }
                socket.close();
            }
        } catch (IOException e) {
            logger.error("[JerryMouse] meet ex", e);
            throw new JerryMouseException(e);
        }
    }
}

```

测试：
```java
public class Main {
    public static void main(String[] args){
        JerryMouseBootstrap bootstrap = new JerryMouseBootstrap();
        bootstrap.start();
    }
}
```
http://127.0.0.1:8080/index.html GET

预计返回：Jerry Mouse index html

需要先mvn clean install， 将index.html 放到target中

注意读取inputStream时，不要使用inputStream.available()， 因为网络数据可能分批到达，实测不能正确读取。

按照现有代码，可以正确读取。

根据路径获得本地资源时，注意区分windows和linux，会有所区别

至此，发现request和response，就是对inputStream和outputStream的一个封装

解析inputStream的内容（也就是http请求），获得url, method等参数
根据url，获取对应的html资源返回。

git log: [jerry-mouse] request, response and static html
