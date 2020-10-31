package cn.wxn.killkill.server.service.impl;

import cn.wxn.killkill.model.entities.User;
import cn.wxn.killkill.model.mapper.UserMapper;
import cn.wxn.killkill.server.service.ItemService;
import cn.wxn.killkill.server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public void register(User user) throws Exception{
        try {
            userMapper.insertSelective(user);
        } catch (Exception e) {
            log.error("注册mapper异常-user:{}",user,e.fillInStackTrace());
            throw new Exception();
        }
    }
}
