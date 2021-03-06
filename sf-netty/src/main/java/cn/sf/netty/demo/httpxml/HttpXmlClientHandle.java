package cn.sf.netty.demo.httpxml;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class HttpXmlClientHandle extends SimpleChannelInboundHandler {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        HttpXmlRequest request = new HttpXmlRequest(null,OrderFactory.create(123));
        ctx.writeAndFlush(request);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx,Object o) throws Exception {
        HttpXmlResponse msg = (HttpXmlResponse)o;
        System.out.println("The client receive response of http header is : " + msg.getHttpResponse().headers().names());
        System.out.println("The client receive response of http body is : " + msg.getResult());
    }
}