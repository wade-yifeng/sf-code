package cn.sf.redis.socket.proto;

/**
 * Created by nijianfeng on 16/12/15.
 */
public enum MessageType {

    LOGIN_REQ((byte)3),
    LOGIN_RESP((byte)4),
    HEARTBEAT_REQ((byte)5),
    HEARTBEAT_RESP((byte)6),

    ;

    public byte value;

    MessageType(byte v){
        this.value = v;
    }

    public byte value(){
        return value;
    }

}
