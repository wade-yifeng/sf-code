package cn.sf.redis.domain;

import cn.sf.redis.enums.ErrorCode;
import lombok.Data;

import java.io.Serializable;

@Data
public class BaseJsonVO<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private int code;
	private String message;
	private T result;

	public static <T> BaseJsonVO<T> ok(T data) {
		BaseJsonVO<T> ret = new BaseJsonVO<T>();
		ret.setCode(ErrorCode.SUCCESS.getIntValue());
		ret.setMessage(ErrorCode.SUCCESS.getDesc());
		ret.setResult(data);
		return ret;
	}

	public static <T> BaseJsonVO<T> fail(ErrorCode code) {
		BaseJsonVO<T> ret = new BaseJsonVO<T>();
		ret.setCode(code.getIntValue());
		ret.setMessage(code.getDesc());
		return ret;
	}

}
