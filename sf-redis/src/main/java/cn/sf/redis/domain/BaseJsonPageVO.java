package cn.sf.redis.domain;

import cn.sf.redis.enums.ErrorCode;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

//T必须是实现序列化接口的对象
@Data
public class BaseJsonPageVO<T extends Serializable> implements Serializable {

	private static final long serialVersionUID = 1L;

	private int code;
	private String message;
	private int total;
	protected boolean hasNext;
	protected List<T> list;

	public static <T extends Serializable> BaseJsonPageVO<T> ok(List<T> data, int total, boolean hasNext) {
		BaseJsonPageVO<T> ret = new BaseJsonPageVO<T>();
		ret.setCode(ErrorCode.SUCCESS.getIntValue());
		ret.setMessage(ErrorCode.SUCCESS.getDesc());
		ret.setList(data);
		ret.setTotal(total);
		ret.setHasNext(hasNext);
		return ret;
	}

	public static <T extends Serializable> BaseJsonPageVO<T> fail(ErrorCode code) {
		BaseJsonPageVO<T> ret = new BaseJsonPageVO<T>();
		ret.setCode(code.getIntValue());
		ret.setMessage(code.getDesc());
		return ret;
	}
}
