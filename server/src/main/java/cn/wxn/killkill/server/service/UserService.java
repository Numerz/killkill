package cn.wxn.killkill.server.service;

import cn.wxn.killkill.model.entities.User;

public interface UserService {
    void register(User user) throws Exception;
}
