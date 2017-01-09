package cn.sf.netty.demo.proto;

import lombok.Data;

@Data
public final class NettyMessage {
    private Header header; //消息头
    private Object body;//消息体

    @Override
    public String toString() {
        return "NettyMessage [header=" + header + "]";
    }
}
