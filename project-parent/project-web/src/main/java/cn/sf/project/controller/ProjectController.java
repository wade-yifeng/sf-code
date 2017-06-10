package cn.sf.project.controller;

import cn.sf.project.aop.annos.AutoLog;
import cn.sf.project.beans.Response;
import cn.sf.project.dto.ProjectDto;
import cn.sf.project.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

/**
 * 缓存管理
 */
@Controller
@RequestMapping("/project")
@Slf4j
@AutoLog
public class ProjectController {

	@Resource
	private ProjectService projectService;

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	@ResponseBody
	public Response<Boolean> add(
			@RequestParam(value = "id") @NotNull Long id,
			@RequestParam(value = "projectName") @NotNull String projectName
	){
		ProjectDto projectDto = new ProjectDto();
		projectDto.setId(id);
		projectDto.setProjectName(projectName);
		return Response.ok(projectService.save(projectDto));
	}

	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
	public String get(
			@PathVariable Long id,
			Model model){
		model.addAttribute("project",projectService.getById(id));
		return "pages/project/project";
	}


}