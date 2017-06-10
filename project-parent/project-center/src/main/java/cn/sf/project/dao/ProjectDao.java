package cn.sf.project.dao;


import cn.sf.project.domain.Project;
import org.apache.ibatis.annotations.*;

/**
 * Created by nijianfeng on 17/6/10.
 */
@Mapper
public interface ProjectDao {

    @Insert("insert into project (id, project_name) values (#{project.id}, #{project.projectName})")
    int save(@Param("project") Project project);

    @Select("select * from project where id = #{id}")
    @ResultMap("ProjectMap")
    Project load(@Param("id") Long id);

}
