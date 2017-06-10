package cn.sf.project.service;

import cn.sf.project.dto.ProjectDto;

/**
 * Created by nijianfeng on 17/6/10.
 */
public interface ProjectService {

    Boolean save(ProjectDto projectDto);

    ProjectDto getById(Long id);

}
