package cn.sf.redis.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by nijianfeng on 16/12/9.
 */
@Controller
@RequestMapping("/test")
public class TestRedis {

    @Autowired
    private TestService testService;

    @ResponseBody
    @RequestMapping(value = "/avail1", method = RequestMethod.GET)
    public List<Integer> getList1(Integer param){
        return testService.getList(2);
    }

    @ResponseBody
    @RequestMapping(value = "/avail2", method = RequestMethod.GET)
    public List<Integer> getList2(Integer param){
        return testService.getList(3);
    }
}
