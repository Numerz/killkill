package cn.wxn.killkill.server.service;

public interface KillService {
    Boolean killItem(Integer killId, Integer userId) throws Exception;

    Boolean killItemRedisLock(Integer killId, Integer userId) throws Exception;

    Boolean killItemRedissonLock(Integer killId, Integer userId) throws Exception;
}
