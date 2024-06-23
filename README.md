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

## 3、servlet
需求分析与背景：
以上仅仅能够返回静态页面。更多时候我们需要返回接口数据，需要服务端处理判断
这时候需要servlet
所谓servlet，就是根据request，对response操作，返回页面或者数据
Servlet 直接处理 HTTP 请求，通过 doGet、doPost 等方法对不同类型的请求进行响应。

Servlet主要接口
```java
public abstract class AbstractJerryMouseServlet extends HttpServlet {

    protected abstract void doGet(HttpServletRequest req, HttpServletResponse resp);

    protected  abstract void doPost(HttpServletRequest req, HttpServletResponse resp);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp){
        if(JerryMouseHttpMethodType.GET.getCode().equalsIgnoreCase(req.getMethod())) {
            this.doGet(req, resp);
            return;
        }

        this.doPost(req, resp);
    }
}
```
再贴一个默认的service实现，就是这么简单
```java
public abstract class HttpServlet extends GenericServlet {
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        long lastModified;
        if (method.equals("GET")) {
            lastModified = this.getLastModified(req);
            if (lastModified == -1L) {
                this.doGet(req, resp);
            } else {
                long ifModifiedSince = req.getDateHeader("If-Modified-Since");
                if (ifModifiedSince < lastModified) {
                    this.maybeSetLastModified(resp, lastModified);
                    this.doGet(req, resp);
                } else {
                    resp.setStatus(304);
                }
            }
        } else if (method.equals("HEAD")) {
            lastModified = this.getLastModified(req);
            this.maybeSetLastModified(resp, lastModified);
            this.doHead(req, resp);
        } else if (method.equals("POST")) {
            this.doPost(req, resp);
        } else if (method.equals("PUT")) {
            this.doPut(req, resp);
        } else if (method.equals("DELETE")) {
            this.doDelete(req, resp);
        } else if (method.equals("OPTIONS")) {
            this.doOptions(req, resp);
        } else if (method.equals("TRACE")) {
            this.doTrace(req, resp);
        } else {
            String errMsg = lStrings.getString("http.method_not_implemented");
            Object[] errArgs = new Object[]{method};
            errMsg = MessageFormat.format(errMsg, errArgs);
            resp.sendError(501, errMsg);
        }
    }
}
```
service 在servlet对应url被访问时调用
那么，如何获得url和servlet的映射关系？这是Tomcat(Servlet容器)的功能，也就是本次需要模仿实现的功能
需要实现一个manager, 实现下面两个接口
```java
public interface IServletManager {
    /**
     * 注册 servlet
     *
     * @param url     url
     * @param servlet servlet
     */
    void register(String url, HttpServlet servlet);

    /**
     * 获取 servlet
     *
     * @param url url
     * @return servlet
     */
    HttpServlet getServlet(String url);
}
```
目前使用web.xml的方式管理，格式形如
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app version="2.4"
         xmlns="http://java.sun.com/xml/ns/j2ee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd">

    <servlet>
        <servlet-name>test</servlet-name>
        <servlet-class>com.github.ljl.jerrymouse.servlet.JerryMouseHttpTestServlet</servlet-class>
    </servlet>

    <servlet-mapping>
        <servlet-name>test</servlet-name>
        <url-pattern>/test</url-pattern>
    </servlet-mapping>

    <servlet>
        <servlet-name>test2</servlet-name>
        <servlet-class>com.github.ljl.jerrymouse.servlet.JerryMouseHttpTest2Servlet</servlet-class>
    </servlet>

    <servlet-mapping>
        <servlet-name>test2</servlet-name>
        <url-pattern>/test2</url-pattern>
    </servlet-mapping>
</web-app>
```

不难想到，可以使用map存储url-servlet映射，作为公共基类，在这里使用组合而不是继承的方式
```java
public class DefaultServletManager implements IServletManager{

    private static Logger logger = LoggerFactory.getLogger(DefaultServletManager.class);

    protected final Map<String, HttpServlet> servletMap = new HashMap<>();

    @Override
    public void register(String url, HttpServlet servlet) {
        logger.info("[JerryMouse] register servlet, url={}, servlet={}", url, servlet.getClass().getName());
        servletMap.put(url, servlet);
    }

    @Override
    public HttpServlet getServlet(String url) {
        return servletMap.get(url);
    }
}
```
并在启动时加载一次web.xml文件，存储在内存
```java
public class WebXmlServletManager implements IServletManager {

    private static Logger logger = LoggerFactory.getLogger(JerryMouseHttpTestServlet.class);

    private IServletManager manager = new DefaultServletManager();

    public WebXmlServletManager() {
        this.loadFromWebXml();
    }

    /**
     * 1. 解析 web.xml
     * 2. 读取对应的 servlet mapping
     * 3. 保存对应的 url + servlet 示例到 servletMap
     */
    private synchronized void loadFromWebXml() {
        // 加载web.xml文件
        this.register(urlPattern, httpServlet);
    }
    @Override
    public void register(String url, HttpServlet servlet) {
        manager.register(url, servlet);
    }

    @Override
    public HttpServlet getServlet(String url) {
        return manager.getServlet(url);
    }
}
```
工具类`WebXmlServletManager` 目前在主程序中初始化一次即可，不会手动调用

如此完成了url-servlet的映射

主流程
```java

/**
 * 对request, response, servletManager的封装
 */
@Data
public class RequestDispatcherContext {

    private JerryMouseRequest request;

    private JerryMouseResponse response;

    // 提供根据url获取Servlet的接口
    private IServletManager servletManager;
}
```
``` java
public class JerryMouseBootstrap {
    public void startService() {
        //...
        while(runningFlag && !serverSocket.isClosed()) {
            try (Socket socket = serverSocket.accept();){
                JerryMouseRequest request = new JerryMouseRequest(socket.getInputStream());
                JerryMouseResponse response = new JerryMouseResponse(socket.getOutputStream());

                // 分发处理
                final RequestDispatcherContext dispatcherContext = new RequestDispatcherContext();
                dispatcherContext.setRequest(request);
                dispatcherContext.setResponse(response);
                dispatcherContext.setServletManager(servletManager);
                this.requestDispatcher.dispatch(dispatcherContext);
                // socket.close();
            } catch (IOException e) {
                logger.error("[JerryMouse] meet exception {}", e);
            }
        }
    }
}
```

`RequestDispatcherManager` 相当于前端控制器，这里关注`servletRequestDispatcher
另两种顾名思义，返回空页面和静态页面，用于测试

```java
public class RequestDispatcherManager implements IRequestDispatcher {

    private final IRequestDispatcher emptyRequestDispatcher = new EmptyRequestDispatcher();
    private final IRequestDispatcher staticHtmlRequestDispatcher = new StaticHtmlRequestDispatcher();
    private final IRequestDispatcher servletRequestDispatcher = new ServletRequestDispatcher();

    @Override
    public void dispatch(RequestDispatcherContext context) {
        final JerryMouseRequest request = context.getRequest();
        // 分发
        String requestUrl = request.getUrl();
        if (StringUtil.isEmpty(requestUrl)) {
            emptyRequestDispatcher.dispatch(context);
        } else {
            if (requestUrl.endsWith(".html")) {
                staticHtmlRequestDispatcher.dispatch(context);
            } else {
                servletRequestDispatcher.dispatch(context);
            }
        }
    }
}
```

```java
public class ServletRequestDispatcher implements IRequestDispatcher {
    private static Logger logger = LoggerFactory.getLogger(ServletRequestDispatcher.class);

    @Override
    public void dispatch(RequestDispatcherContext context) {
        JerryMouseRequest request = context.getRequest();
        JerryMouseResponse response = context.getResponse();
        IServletManager servletManager = context.getServletManager();

        // 直接和 servlet 映射
        String requestUrl = request.getUrl();
        // 此处用到了之前注册的url-servlet
        HttpServlet httpServlet = servletManager.getServlet(requestUrl);
        if(Objects.isNull(httpServlet)) {
            logger.warn("[JerryMouse] requestUrl={} mapping not found", requestUrl);
            response.write(JerryMouseHttpUtils.http404Resp());
        } else {
            // 正常的逻辑处理
            try {
                // 用到了策略模式
                httpServlet.service(request, response);
            } catch (Exception e) {
                logger.error("[JerryMouse] http servlet handle meet ex", e);
                throw new JerryMouseException(e);
            }
        }
    }
}
```

如此，`httpServlet.service(request, response)` 后，一次调用完成

回顾调用链

1、容器初始化

1.1 socket监听8080端口

1.2 初始化WebXmlServletManager，扫描web.xml, 存储url-servlet map

1.3 初始化RequestDispatcherManager，用于之后分发请求，选择使用静态页面还是servlet

1.4 初始化ServletRequestDispatcher，用于分配到不同的servlet

2、发送一个请求

2.1 通过socket获得InputStream和OutputStream，存放到context

2.2 RequestDispatcherManager通过context获得request，根据url判断
是请求静态页面还是servlet

2.3 如请求servlet， ServletRequestDispatcher 负责分发，从context中获取初始化的
ServletManager(此处即WebXmlServletManager)，通过接口getServlet(url) 获取对应servlet

2.4 获取到的servlet.service(request, response); 其中自己实现的servlet需要继承HttpServlet

2.5 在service()中调用对应的doGet()或者doPost(), response.write()返回内容

```
"pool-1-thread-1@1208" prio=5 tid=0xd nid=NA runnable
  java.lang.Thread.State: RUNNABLE
	  at com.github.ljl.jerrymouse.servlet.JerryMouseHttpTestServlet.doGet(JerryMouseHttpTestServlet.java:25)
	  at com.github.ljl.jerrymouse.servlet.AbstractJerryMouseServlet.service(AbstractJerryMouseServlet.java:27)
	  at javax.servlet.http.HttpServlet.service(HttpServlet.java:750)
	  at com.github.ljl.jerrymouse.dispatcher.ServletRequestDispatcher.dispatch(ServletRequestDispatcher.java:41)
	  at com.github.ljl.jerrymouse.dispatcher.RequestDispatcherManager.dispatch(RequestDispatcherManager.java:32)
	  at com.github.ljl.jerrymouse.bootstrap.JerryMouseBootstrap.startService(JerryMouseBootstrap.java:95)
	  at com.github.ljl.jerrymouse.bootstrap.JerryMouseBootstrap.lambda$start$0(JerryMouseBootstrap.java:69)
	  at com.github.ljl.jerrymouse.bootstrap.JerryMouseBootstrap$$Lambda$1.345281752.run(Unknown Source:-1)
	  at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	  at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	  at java.lang.Thread.run(Thread.java:748)
```
补充说明

httpServlet.service(request, response)要求request和response，实现HttpServletRequest和HttpServletResponse接口
然而，我们自己封装的简易request，response并没有实现接口中的所有方法, 不能过编译
下面以Request为例说明处理方法：

可以使用适配器模式，新建一个RequestAdaptor抽象类，实现HttpRequest的暂时不需要使用的接口
仅需要默认实现，通过编译，不需要具体功能。而我们的Request只需要实现类似getMethod这样的，
被其他模块用到的方法。

其他多余的接口，目前并不打算用到，因此可以都先改为抛异常。

Request继承Adaptor，实现getMethod等已经使用到的方法。

![requestAdaptorDiagram.png](images/v0.3/requestAdaptorDiagram.png)

当然也可以使用组合的方式

至此，已经完成了最mini版本的servlet容器

测试：

```bash
mvn clean
mvn install

启动Main

# 使用postman等工具测试
> GET http://127.0.0.1:8080/test
JerryMouseHttpTestServlet-get
> POST http://127.0.0.1:8080/test
JerryMouseHttpTestServlet-post
> GET http://127.0.0.1:8080/test2
JerryMouseHttpTestServlet2-get
> GET http://127.0.0.1:8080/test3
404 Not Found: The requested resource was not found on this server.
> GET http://127.0.0.1:8080/index.html
Jerry Mouse index html
> GET http://127.0.0.1:8080/1.html
1 html
> GET http://127.0.0.1:8080/2.html
404 Not Found: The requested resource was not found on this server.

```

记为0.3版本

[uml](./uml/jerrymouse_v0.3.puml)

参考文献：[houbb 从零手写实现 tomcat](https://houbb.github.io/2016/11/07/web-server-tomcat-05-hand-write-servlet-web-xml)

## step 4、nio netty
需求：bootstrap中的socket目前用的是最简单的bio，会阻塞
要求改为nio
分别完成手写nio和使用netty框架两个版本
将旧版实现改为JerryMouseBootstrapBio, 用作复习

nio 代码，此处有遗留问题 TODO
```java
class JerryMouseBootStrap {
    private void startService() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);

            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();
                    if (key.isAcceptable()) {
                        handleAccept(key);
                    } else if (key.isReadable()) {
                        handleRead(key);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("[JerryMouse] start meet exception {}", e);
            throw new JerryMouseException(e);
        }
    }
    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(key.selector(), SelectionKey.OP_READ);
    }

    private void handleRead(SelectionKey key) {
        // TODO: 异步执行会有问题
        // threadPool.execute(() -> {
            SocketChannel clientChannel = (SocketChannel) key.channel();
            if (!clientChannel.isOpen()) {
                return;
            }
            try {
                JerryMouseRequest request = new JerryMouseRequest(clientChannel);
                JerryMouseResponse response = new JerryMouseResponse(clientChannel);
                RequestDispatcherContext dispatcherContext = new RequestDispatcherContext();
                dispatcherContext.setRequest(request);
                dispatcherContext.setResponse(response);
                dispatcherContext.setServletManager(servletManager);
                requestDispatcher.dispatch(dispatcherContext);
            } finally {
                try {
                    if (clientChannel.isOpen()) {
                        key.interestOps(key.interestOps() | SelectionKey.OP_READ);
                        key.selector().wakeup(); // 确保selector从阻塞状态返回
                    } else {
                        key.cancel();
                    }
                } catch (CancelledKeyException e) {
                    logger.error("Key has been cancelled", e);
                }
            }
        // });
    }
}

public class JerryMouseRequest extends JerryMouseRequestAdaptor {
    // 省略部分字段和接口
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
}

public class JerryMouseResponse extends JerryMouseResponseAdaptor {
    private static Logger logger = LoggerFactory.getLogger(JerryMouseBootstrap.class);

    private SocketChannel clientChannel;

    public JerryMouseResponse(SocketChannel clientChannel) {
        this.clientChannel = clientChannel;
    }

    public void write(String data){
        ByteBuffer buffer = ByteBuffer.wrap(data.getBytes());
        while (buffer.hasRemaining()) {
            try {
                clientChannel.write(buffer);
                // 这句不能省略
                clientChannel.close();
            }
            catch (IOException e) {
                logger.error("[JerryMouse] meet exception");
                throw new JerryMouseException(e);
            }
        }
    }
}
```

区别在于，使用SocketChannel，而不是直接使用InputStream

git log：[jerry-mouse] 4.1.1-nio 

## 4.2 netty

netty框架是
```java

```
核心组件(回答来自chatgpt)
- Channel：Netty 用于网络 I/O 操作的基本抽象，表示一个打开的连接，如 TCP 连接、文件等。通过 Channel 可以进行数据的读写操作。
EventLoop

- EventLoop：一个处理 I/O 操作的单线程执行器，每个 EventLoop 负责一个或多个 Channel 的 I/O 操作。EventLoopGroup 是 EventLoop 的集合，通常用于管理一组 EventLoop。
ChannelHandler

- ChannelHandler：是处理 I/O 事件的回调接口，可以实现自定义的业务逻辑，如编码、解码、业务处理等。
ChannelPipeline

- ChannelPipeline：是 ChannelHandler 的链，管理着 ChannelHandler 的调用顺序。每个 Channel 都有一个独立的 ChannelPipeline。
Bootstrap 和 ServerBootstrap：

这两个类用于引导客户端和服务器，配置各种参数并启动应用。

使用方法 代码欣赏一下，主要是重写channelRead
```java
class JerryMouseBootStrap
{
    public void startService() {
        logger.info("[JerryMouse] start listen on port {}", port);
        logger.info("[JerryMouse] visit url http://{}:{}", LOCAL_HOST, port);

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //worker 线程池的数量默认为 CPU 核心数的两倍
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new JerryMouseServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            ChannelFuture future = serverBootstrap.bind(port).sync();

            // Wait until the server socket is closed.
            future.channel().closeFuture().sync();
            logger.info("DONE");
        } catch (InterruptedException e) {
            logger.error("JerryMouse start meet exception");
            throw new JerryMouseException(e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
public class JerryMouseServerHandler extends ChannelInboundHandlerAdapter {
    private static Logger logger = LoggerFactory.getLogger(JerryMouseRequestUtils.class);

    /**
     * servlet 管理
     *
     * @since 0.3.0
     */
    private final IServletManager servletManager = new WebXmlServletManager();

    /**
     * 请求分发
     *
     * @since 0.3.0
     */
    private final IRequestDispatcher requestDispatcher = new RequestDispatcherManager();

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        String requestString = new String(bytes, Charset.defaultCharset());
        logger.info("[JerryMouse] channelRead requestString={}", requestString);


        // 获取请求信息
        RequestInfoBo requestInfoBo = JerryMouseRequestUtils.buildRequestInfoBo(requestString);
        IRequest request = new JerryMouseRequest(requestInfoBo.getMethod(), requestInfoBo.getUrl());
        IResponse response = new JerryMouseResponse(context);

        // 分发调用
        final RequestDispatcherContext dispatcherContext = new RequestDispatcherContext();
        dispatcherContext.setRequest(request);
        dispatcherContext.setResponse(response);
        dispatcherContext.setServletManager(servletManager);
        requestDispatcher.dispatch(dispatcherContext);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) throws Exception {
        context.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
        logger.error("JerryMouseServerHandler cause exception ", cause);
        context.close();
    }
}

```
channelRead调用栈
```
"nioEventLoopGroup-3-1@1842" prio=10 tid=0xf nid=NA runnable
  java.lang.Thread.State: RUNNABLE
	  at com.github.ljl.jerrymouse.bootstrap.JerryMouseServerHandler.channelRead(JerryMouseServerHandler.java:49)
	  at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:444)
	  at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:420)
	  at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:412)
	  at io.netty.channel.DefaultChannelPipeline$HeadContext.channelRead(DefaultChannelPipeline.java:1410)
	  at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:440)
	  at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:420)
	  at io.netty.channel.DefaultChannelPipeline.fireChannelRead(DefaultChannelPipeline.java:919)
	  at io.netty.channel.nio.AbstractNioByteChannel$NioByteUnsafe.read(AbstractNioByteChannel.java:166)
	  at io.netty.channel.nio.NioEventLoop.processSelectedKey(NioEventLoop.java:788)
	  at io.netty.channel.nio.NioEventLoop.processSelectedKeysOptimized(NioEventLoop.java:724)
	  at io.netty.channel.nio.NioEventLoop.processSelectedKeys(NioEventLoop.java:650)
	  at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:562)
	  at io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:997)
	  at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74)
	  at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	  at java.lang.Thread.run(Thread.java:748)

```
// TODO: netty内部源码分析

架构修改：
request和response，实际需要的是一些接口，例如getMethod，getUrl
因此加一层接口，以request为例
```java
public interface IRequest extends HttpServletRequest {
    /**
     * 获取请求地址
     * @return url
     */
    String getUrl();

    /**
     * 获取方法
     * @return method
     */
    String getMethod();
}
public abstract class JerryMouseRequestAdaptor implements IRequest {
    // 代码略
}
@Data
@AllArgsConstructor
public class JerryMouseRequest extends JerryMouseRequestAdaptor {
    private static Logger logger = LoggerFactory.getLogger(JerryMouseRequest.class);

    private String method;

    private String url;
}
```
并以接口的形式使用

删除了旧版代码

[uml v0.4.2](./uml/jerrymouse_v0.4.2.puml)

测试方式相同，功能测试正常

// TODO：性能测试

记为0.4.2

git log: [jerrymouse] 0.4.2-using netty

tag: v0.4.2

## 5、解析war包 类加载（难点）
需求：
目前仅加载自己写的servlet，现在要求能解析处理其他war包

新建一个web-demo项目，写一个IndexServlet，并打包
```
mvn clean install
```
生成 web-demo目录 和 web-demo.war

根据类路径（非当前项目）加载类信息

问题: 测试servlet代码如下
```java
public class IndexServlet extends HttpServlet {
    public IndexServlet() {
    }
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter(); // null 
        resp.setContentType("text/html");
        out.println("<h1>servlet index get</h1>");
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<h1>servlet index post</h1>");
    }
}
```
继承HttpServlet
但本次测试的Servlet逻辑自己实现。

而JerryMouse服务器，和IndexServlet的公共接口，只有HttpServlet

但实际上，JerryMouseResponse根本没有实现完整的HttpServlet，而是使用适配器

JerryMouseResponseAdaptor蒙混过关
适配器中，没有正确实现resp.getWriter()或者getOutputStream()接口
（而是无效接口，再次体会一下接口，协议的作用）
因此没法进行

目前IndexServlet只能基于HttpServlet的接口，而内部的Servlet是这样写的
```java
public class JerryMouseHttpTestServlet extends AbstractJerryMouseServlet {

    private static Logger logger = LoggerFactory.getLogger(JerryMouseHttpTestServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String content = "JerryMouseHttpTestServlet-get";

        JerryMouseResponse response = (JerryMouseResponse) resp;
        response.write(JerryMouseHttpUtils.http200Resp(content));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String content = "JerryMouseHttpTestServlet-post";

        JerryMouseResponse response = (JerryMouseResponse) resp;
        response.write(JerryMouseHttpUtils.http200Resp(content));
    }
}
```

因此，Servlet需要持有JerryMouseResponse的引用。。。吗？引入相关的库？
有种立即见效的方式，就是使用反射强行调用。。。你就说能不能用吧

```java
public class IndexServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        writeToResponse(resp,"servlet index get");
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        writeToResponse(resp,"servlet index post");
    }
    private void writeToResponse(HttpServletResponse resp, String content) {
        try {
            Class<?> responseClass = resp.getClass();
            Method writeMethod = responseClass.getMethod("write", String.class);
            writeMethod.invoke(resp, IndexServlet.http200Resp(content));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String http200Resp(String rawText) {
        String format = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                "%s";

        return String.format(format, rawText);
    }
}
```

好！下面进行测试

```bash
# project web-demo
mvn clean install
将web-demo.war包放到jerry-mouse项目的test/webapps下
# project jerry-mouse
根据项目所在位置,修改 com.github.ljl.Main 中的 baseWarDir
mvn clean install

启动 com.github.ljl.Main

# 功能测试
> GET http://127.0.0.1:8080/web-demo/index
servlet index get
> POST http://127.0.0.1:8080/web-demo/index
servlet index post
> GET http://127.0.0.1:8080/test
JerryMouseHttpTestServlet-get
> POST http://127.0.0.1:8080/test
JerryMouseHttpTestServlet-post
> POST http://127.0.0.1:8080/test2
JerryMouseHttpTestServlet2-post
> GET http://127.0.0.1:8080/index.html
Jerry Mouse index html
```

```bash
git log:
jerry-mouse:  [jerry-mouse] v0.5.0 load-other-webapp
web-demo:     [web-demo] v0.5.0 load-other-webapp
```
