package cn.wxn.killkill.server.controller;

import cn.wxn.killkill.api.enums.StatusCode;
import cn.wxn.killkill.api.response.BaseResponse;
import cn.wxn.killkill.server.dto.KillDto;
import cn.wxn.killkill.server.service.KillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
public class KillController {
    private static final Logger log = LoggerFactory.getLogger(KillController.class);

    private static final String prefix = "/kill";

    private KillService killService;

    @Autowired
    public void setKillService(KillService killService) {
        this.killService = killService;
    }

    @RequestMapping(value = prefix + "/execute", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse execute(@RequestBody @Validated KillDto killDto, BindingResult result, HttpSession session){
        if (result.hasErrors() || killDto.getKillId() <= 0){
            return new BaseResponse(StatusCode.InvalidParams);
        }
//        Integer userId = killDto.getUserId();
        Integer userId = (Integer) session.getAttribute("uid");

        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            Boolean res = killService.killItem(killDto.getKillId(), userId);
            if (!res){
                return new BaseResponse(StatusCode.Fail.getCode(), "商品抢购失败");
            }
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }

        return response;
    }

    /**
     * InnoDB行锁
     * @param killDto
     * @param result
     * @param session
     * @return
     */
    @RequestMapping(value = prefix + "/execute/unlock", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public BaseResponse executeUnlock(@RequestBody @Validated KillDto killDto, BindingResult result, HttpSession session){
        if (result.hasErrors() || killDto.getKillId() <= 0){
            return new BaseResponse(StatusCode.InvalidParams);
        }
        Integer userId = killDto.getUserId();
//        Integer userId = (Integer) session.getAttribute("uid");

        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {

            //不加分布式锁
            Boolean res = killService.killItem(killDto.getKillId(), userId);
            if (!res){
                return new BaseResponse(StatusCode.Fail.getCode(), "InnoDB-商品抢购失败");
            }
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }

        return response;
    }

    /**
     * Redis分布式锁
     * @param killDto
     * @param result
     * @param session
     * @return
     */
    @RequestMapping(value = prefix + "/execute/lock", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public BaseResponse executeLock(@RequestBody @Validated KillDto killDto, BindingResult result, HttpSession session){
        if (result.hasErrors() || killDto.getKillId() <= 0){
            return new BaseResponse(StatusCode.InvalidParams);
        }
        Integer userId = killDto.getUserId();
//        Integer userId = (Integer) session.getAttribute("uid");

        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {

            //Redis分布式锁
            Boolean res = killService.killItemRedisLock(killDto.getKillId(), userId);
            if (!res){
                return new BaseResponse(StatusCode.Fail.getCode(), "Redis-商品抢购失败");
            }
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }

        return response;
    }

    /**
     * Redisson分布式锁
     * @param killDto
     * @param result
     * @param session
     * @return
     */
//    consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    @RequestMapping(value = prefix + "/execute/locklock", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse executeLockLock(@RequestBody @Validated KillDto killDto, BindingResult result, HttpSession session){
        if (result.hasErrors() || killDto.getKillId() <= 0){
            return new BaseResponse(StatusCode.InvalidParams);
        }
        Integer userId = killDto.getUserId();
//        Integer userId = (Integer) session.getAttribute("uid");

        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {

            //Redisson分布式锁
            Boolean res = killService.killItemRedissonLock(killDto.getKillId(), userId);
            if (!res){
                return new BaseResponse(StatusCode.Fail.getCode(), "Redisson-商品抢购失败");
            }
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }

        return response;
    }


    @RequestMapping(value = prefix + "/execute/success")
    public String success(){
        return "executeSuccess";
    }

    @RequestMapping(value = prefix + "/execute/fail")
    public String fail(){
        return "executeFail";
    }
}
