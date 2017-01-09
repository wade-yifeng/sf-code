package cn.sf.redis.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class CacheInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String key;

	private String url;

	private long count;

	private String expireTime;

	private String ttl;

	private List<FieldInfo> fieldInfos = new ArrayList<FieldInfo>();

	public void addFieldInfo(String field, String url) {
		FieldInfo fieldInfo = new FieldInfo();
		fieldInfo.setField(field);
		fieldInfo.setUrl(url);
		fieldInfos.add(fieldInfo);
	}

}

