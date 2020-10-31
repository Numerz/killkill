package cn.wxn.killkill.server.controller;

import cn.wxn.killkill.model.entities.User;
import cn.wxn.killkill.server.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    private static final Logger log= LoggerFactory.getLogger(UserController.class);

    private UserService userService;

    private Environment env;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }

    /**
     * 跳到登录页
     * @return
     */
    @RequestMapping(value = {"/to/login","/unauth"})
    public String toLogin(){
        return "login";
    }

    /**
     * 登录认证
     * @param userName
     * @param password
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(@RequestParam String userName, @RequestParam String password, ModelMap modelMap){
        String errorMsg="";
        try {
            if (!SecurityUtils.getSubject().isAuthenticated()){
                String newPsd=new Md5Hash(password,env.getProperty("shiro.encrypt.password.salt")).toString();
                UsernamePasswordToken token=new UsernamePasswordToken(userName,newPsd);
                SecurityUtils.getSubject().login(token);
            }
        }catch (UnknownAccountException e){
            errorMsg=e.getMessage();
            modelMap.addAttribute("userName",userName);
        }catch (DisabledAccountException e){
            errorMsg=e.getMessage();
            modelMap.addAttribute("userName",userName);
        }catch (IncorrectCredentialsException e){
            errorMsg=e.getMessage();
            modelMap.addAttribute("userName",userName);
        }catch (Exception e){
            errorMsg="用户登录异常，请联系管理员!";
            e.printStackTrace();
        }
        if (StringUtils.isBlank(errorMsg)){
            return "redirect:/index";
        }else{
            modelMap.addAttribute("errorMsg",errorMsg);
            return "login";
        }
    }


    @RequestMapping(value = "/register/{username}/{password}/{phone}/{email}")
    public String register(@PathVariable String username, @PathVariable String password, @PathVariable String phone, @PathVariable String email){
        try {
            String newPsd = new Md5Hash(password,env.getProperty("shiro.encrypt.password.salt")).toString();
            User user = new User(username, newPsd, phone, email);
            userService.register(user);
        } catch (Exception e) {
            return "redirect:/register";
        }
        return "regisSuccess";
    }

    /**
     * 退出登录
     * @return
     */
    @RequestMapping(value = "/logout")
    public String logout(){
        SecurityUtils.getSubject().logout();
        return "login";
    }

    public static void main(String[] args) {
        String newPsd = new Md5Hash("12345","11299c42bf954c0abb373efbae3f6b26").toString();
        System.out.println(newPsd);
    }
}
