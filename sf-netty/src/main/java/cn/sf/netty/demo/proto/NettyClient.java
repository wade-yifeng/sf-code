package cn.sf.netty.demo.proto;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class NettyClient {

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    EventLoopGroup group = new NioEventLoopGroup();

    public void connect(int port, String host) throws Exception {
        // 配置客户端NIO线程组
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer() {
                        @Override
                        public void initChannel(Channel ch)throws Exception {
                            //NettyMessageDecoder用于Netty消息解码，
                            //为了防止由于单条消息过大导致的内存溢出或者畸形码流导致解码错位引起内存分配失败，
                            //我们对单条消息最大长度进行了上限限制。
                            ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4));
                            //Netty消息编码器，用于协议消息的自动编码。
                            ch.pipeline().addLast("MessageEncoder",new NettyMessageEncoder());
                            //读超时Handler
                            ch.pipeline().addLast("readTimeoutHandler",new ReadTimeoutHandler(50));
                            //握手请求Handler
                            ch.pipeline().addLast("LoginAuthHandler",new LoginAuthReqHandler());
                            //心跳消息Handler
                            ch.pipeline().addLast("HeartBeatHandler",new HeartBeatReqHandler());
                        }
                    });
            // 发起异步连接操作
            ChannelFuture future = b.connect(
                    new InetSocketAddress(host, port),
                    new InetSocketAddress(NettyConstant.LOCALIP,NettyConstant.LOCAL_PORT)
            ).sync();

            future.channel().closeFuture().sync();
        } finally {
            // 所有资源释放完成之后，清空资源，再次发起重连操作
//            executor.execute(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        TimeUnit.SECONDS.sleep(5);
//                        try {
//                            connect(NettyConstant.PORT, NettyConstant.REMOTEIP);// 发起重连操作
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
        }
    }

    public static void main(String[] args) throws Exception {
        new NettyClient().connect(NettyConstant.PORT, NettyConstant.REMOTEIP);
    }
}