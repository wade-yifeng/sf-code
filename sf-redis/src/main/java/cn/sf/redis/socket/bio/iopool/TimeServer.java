package cn.sf.redis.socket.bio.iopool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class TimeServer {
    //首先创建一个时间服务器处理类的线程池，当接收到新的客户端连接的时候，将请求Socket封装成一个Task，
    //然后调用线程池的execute方法执行，从而避免了每个请求接入都创建一个新的线程。
    public static void main(String[] args) throws IOException {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // 采用默认值
            }
        }
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            System.out.println("The time server is start in port : " + port);
            Socket socket = null;

            TimeServerHandlerExecutePool singleExecutor = new TimeServerHandlerExecutePool(50,10000);
            //创建I/O任务线程池
            while (true) {
                socket = server.accept();
                singleExecutor.execute(new TimeServerHandler(socket));
            }
        } finally {
            if (server != null) {
                System.out.println("The time server close");
                server.close();
                server = null;
            }
        }
    }
}