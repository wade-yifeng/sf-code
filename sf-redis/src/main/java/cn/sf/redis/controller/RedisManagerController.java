package cn.sf.redis.controller;

import cn.sf.redis.domain.BaseJsonVO;
import cn.sf.redis.domain.CacheInfo;
import cn.sf.redis.service.RedisHelperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 缓存管理
 */
@Controller
@RequestMapping("/redis/manager")
@Slf4j
public class RedisManagerController {


	@Autowired
	private RedisHelperService redisHelperService;

	@RequestMapping(value = "/listKMCache", method = RequestMethod.GET)
	public String listKMCache(
			Model model,
			Boolean level) throws Exception {

		List<CacheInfo> cacheInfos = redisHelperService.getKMCacheList().getResult();
		if(cacheInfos.size()>0) {
			model.addAttribute("cacheInfos", cacheInfos);
		}
		if(level){
			model.addAttribute("level",level);
		}

		return "pages/redis/kmCacheList";
	}

	private void redirctToListKMCache(HttpServletResponse response,String level) {
		try {
			response.sendRedirect("/redis/manager/listKMCache?level="+level);
		} catch (IOException e) {
			log.error("redirect err", e);
		}
	}


	@RequestMapping(value = "/clearKMByKey", method = RequestMethod.GET)
	public @ResponseBody BaseJsonVO clearKMByKey(
			HttpServletResponse response,
			@RequestParam(value = "groupName") String groupName,
			@RequestParam(value = "key", required = false) String key,
			@RequestParam(value = "redirect" , required = false) String redirect,
			@RequestParam(value = "level" , required = false) String level
	){
		BaseJsonVO ret = redisHelperService.clearKMByKey(groupName,key);
		if(ret.getCode()==200) {
			if ("true".equals(redirect)) {
				this.redirctToListKMCache(response, level);
			}
		}
		return ret;
	}

	@RequestMapping(value = "/clearKMByGroupName", method = RequestMethod.GET)
	public @ResponseBody BaseJsonVO clearKMByGroupName(
			HttpServletResponse response,
			@RequestParam(value = "groupName") String groupName,
			@RequestParam(value = "redirect" , required = false) String redirect,
			@RequestParam(value = "level" , required = false) String level
	){
		BaseJsonVO ret = redisHelperService.clearKMByGroupName(groupName);
		if(ret.getCode()==200) {
			if ("true".equals(redirect)) {
				this.redirctToListKMCache(response, level);
			}
		}
		return ret;
	}

}