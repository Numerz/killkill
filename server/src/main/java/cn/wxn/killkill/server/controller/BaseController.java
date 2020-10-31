package cn.wxn.killkill.server.controller;

import cn.wxn.killkill.api.enums.StatusCode;
import cn.wxn.killkill.api.response.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BaseController {

    private static final Logger log = LoggerFactory.getLogger(BaseController.class);

    private static final String prefix = "/base";

    @RequestMapping(value = prefix + "/welcome", method = RequestMethod.GET)
    public String welcome(String name, ModelMap modelMap){
        if (StringUtils.isEmpty(name)){
            name = "welcome!!!";
        }
        modelMap.put("name", name);
        return "welcome";
    }

    @RequestMapping(value = prefix + "/data", method = RequestMethod.GET)
    @ResponseBody
    public String data(String name){
        if (StringUtils.isEmpty(name)){
            name = "welcome!!!";
        }
        return name;
    }

    @RequestMapping(value = prefix + "/response", method = RequestMethod.GET)
    @ResponseBody
    public BaseResponse response(String name){
        BaseResponse baseResponse = new BaseResponse(StatusCode.Success);
        if (StringUtils.isEmpty(name)){
            name = "welcome!!!";
        }
        baseResponse.setData(name);
        return baseResponse;
    }

    @RequestMapping(value = prefix + "/error", method = RequestMethod.GET)
    public String error(){
        return "error";
    }
}
