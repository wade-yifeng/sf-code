package cn.sf.utils.mybatis;

import cn.sf.utils.page.Paging;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class BaseDao<T> extends SqlSessionDaoSupport {
    protected static final String CREATE = "create";
    protected static final String CREATES = "creates";
    protected static final String DELETE = "delete";
    protected static final String DELETES = "deletes";
    protected static final String UPDATE = "update";
    protected static final String LOAD = "load";
    protected static final String LOADS = "loads";
    protected static final String LIST = "list";
    protected static final String COUNT = "count";
    protected static final String PAGING = "paging";
    public final String nameSpace;

    private final static List EMPTYLIST= Lists.newArrayList();

    public BaseDao() {
        Type type = BaseDao.class.getGenericSuperclass();
        if(type instanceof ParameterizedType) {
            this.nameSpace = ((Class)((ParameterizedType)type).getActualTypeArguments()[0]).getSimpleName();
        } else {
            type = BaseDao.class.getSuperclass().getGenericSuperclass();
            this.nameSpace = ((Class)((ParameterizedType)type).getActualTypeArguments()[0]).getSimpleName();
        }
    }

    public void init (SqlSessionFactory factory) {
        super.setSqlSessionFactory(factory);
    }

    public abstract void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory);

    public Boolean create(T t) {
        return this.getSqlSession().insert(this.sqlId("create"), t) == 1;
    }

    public Integer creates(List<T> ts) {
        return this.getSqlSession().insert(this.sqlId("creates"), ts);
    }

    public Integer creates(T... tn) {
        return this.getSqlSession().insert(this.sqlId("creates"), Arrays.asList(tn));
    }

    public Boolean delete(Long id) {
        return this.getSqlSession().delete(this.sqlId("delete"), id) == 1;
    }

    public Integer deletes(List<Long> ids) {
        return this.getSqlSession().delete(this.sqlId("deletes"), ids);
    }

    public Integer deletes(Long... idn) {
        return this.getSqlSession().delete(this.sqlId("deletes"),Arrays.asList(idn));
    }

    //更新的条件具有唯一约束
    public Boolean update(T t) {
        return this.getSqlSession().update(this.sqlId("update"),t) == 1;
    }

    public T load(Integer id) {
        return this.load(Long.valueOf(id));
    }

    public T load(Long id) {
        return this.getSqlSession().selectOne(this.sqlId("load"), id);
    }

    public List<T> loads(List<Long> ids) {
        return (CollectionUtils.isEmpty(ids)? EMPTYLIST:this.getSqlSession().selectList(this.sqlId("loads"), ids));
    }

    public List<T> loads(Long... idn) {
        return this.getSqlSession().selectList(this.sqlId("loads"), Arrays.asList(idn));
    }

    public List<T> listAll() {
        return this.list((T) null);
    }

    public List<T> list(T t) {
        return this.getSqlSession().selectList(this.sqlId("list"), t);
    }

    public List<T> list(Map<String, Object> criteria) {
        return this.getSqlSession().selectList(this.sqlId("list"), criteria);
    }

    public Paging<T> paging(Integer offset, Integer limit) {
        return this.paging(offset, limit, Maps.newHashMap());
    }

    public Paging<T> paging(Integer offset, Integer limit, Map<String, Object> criteria) {
        if(criteria == null) {
            criteria = Maps.newHashMap();
        }

        Long total = this.getSqlSession().selectOne(this.sqlId("count"), criteria);
        if(total <= 0L) {
            return Paging.empty();
        } else {
            criteria.put("offset", offset);
            criteria.put("limit", limit);
            List<T> datas = this.getSqlSession().selectList(this.sqlId("paging"), criteria);
            return Paging.gen(total, datas);
        }
    }

    public Paging<T> paging(Map<String, Object> criteria) {
        if(criteria == null) {
            criteria = Maps.newHashMap();
        }

        Long total = this.getSqlSession().selectOne(this.sqlId("count"), criteria);
        if(total <= 0L) {
            return Paging.empty();
        } else {
            List<T> datas = this.getSqlSession().selectList(this.sqlId("paging"), criteria);
            return Paging.gen(total, datas);
        }
    }

    protected String sqlId(String id) {
        return this.nameSpace + "." + id;
    }

}
