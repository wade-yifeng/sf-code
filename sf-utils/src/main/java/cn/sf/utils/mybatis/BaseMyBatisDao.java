package cn.sf.utils.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;

import javax.annotation.Resource;

public abstract class BaseMyBatisDao<T> extends BaseDao<T> {

    public BaseMyBatisDao() {
        super();
    }

    @Resource(name = "defaultSqlSessionFactory")
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory)
    {
        super.init(sqlSessionFactory);
    }
}