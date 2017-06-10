package cn.sf.project.outer;

import cn.sf.project.dao.ProjectDao;
import cn.sf.project.domain.Project;
import cn.sf.project.dto.ProjectDto;
import cn.sf.project.service.ProjectService;
import cn.sf.project.utils.BeanCopyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by nijianfeng on 17/6/10.
 */
@Service("projectService")
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    @Resource
    private ProjectDao projectDao;

    @Override
    public Boolean save(ProjectDto projectDto) {
        return projectDao.save(BeanCopyUtil.genBean(projectDto, Project.class))==1;
    }

    @Override
    public ProjectDto getById(Long id) {
        return BeanCopyUtil.genBean(projectDao.load(id),ProjectDto.class);
    }
}
