package cn.wxn.killkill.server.service.impl;

import ch.qos.logback.core.net.SyslogConstants;
import cn.wxn.killkill.model.entities.ItemKill;
import cn.wxn.killkill.model.entities.ItemKillSuccess;
import cn.wxn.killkill.model.mapper.ItemKillMapper;
import cn.wxn.killkill.model.mapper.ItemKillSuccessMapper;
import cn.wxn.killkill.server.enums.SysConstant;
import cn.wxn.killkill.server.service.ItemService;
import cn.wxn.killkill.server.service.KillService;
import cn.wxn.killkill.server.utils.RandomUtil;
import cn.wxn.killkill.server.utils.SnowFlake;
import org.joda.time.DateTime;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class KillServiceImpl implements KillService {

    private static final Logger log = LoggerFactory.getLogger(ItemService.class);

    private ItemKillMapper itemKillMapper;

    private ItemKillSuccessMapper itemKillSuccessMapper;

    private final SnowFlake snowFlake = new SnowFlake(2, 3);

    private RedissonClient redissonClient;

    @Autowired
    public void setRedissonClient(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Autowired
    public void setItemKillMapper(ItemKillMapper itemKillMapper) {
        this.itemKillMapper = itemKillMapper;
    }

    @Autowired
    public void setItemKillSuccessMapper(ItemKillSuccessMapper itemKillSuccessMapper) {
        this.itemKillSuccessMapper = itemKillSuccessMapper;
    }

    @Transactional
    @Override
    public Boolean killItem(Integer killId, Integer userId) throws Exception {
        boolean result = false;

        //TODO:行锁
        if (itemKillSuccessMapper.selectByKillUserForUpdate(killId, userId) == 0){
            ItemKill itemKill = itemKillMapper.selectByIdForUpdate(killId);

            //TODO:秒杀核心代码
            if (itemKill != null && itemKill.getCanKill() == 1){
                int res = itemKillMapper.updateKillItem(killId);
                if (res > 0){
                    commonRecordKillSuccessInfo(itemKill,userId);
                    result = true;
                }
            }
        }else {
            throw new Exception("您已经抢购过该商品");
        }
        return result;
    }

    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public Boolean killItemRedisLock(Integer killId, Integer userId) throws Exception {

        boolean result = false;

        if (itemKillSuccessMapper.selectByKillUser(killId, userId) == 0){

            //TODO:Redis分布式锁，此方法会出现票抢不完的情况
            ValueOperations valueOperations =stringRedisTemplate.opsForValue();
            StringBuffer sb = new StringBuffer();
            final String key = sb.append(killId.toString()).append("-RedisLock").toString();
            final String val = String.valueOf(snowFlake.nextId());
            Boolean cacheRes = null;

            try {
                cacheRes = valueOperations.setIfAbsent(key, val,10, TimeUnit.SECONDS);
            } catch (Exception e) {
                stringRedisTemplate.delete(key);
                log.info("userId: {}, 设置锁异常", userId);
                e.printStackTrace();
            }


            //TODO:此时节点宕机key无法释放，会导致锁死

            if (cacheRes) {
                stringRedisTemplate.expire(key, 10, TimeUnit.SECONDS);

                try {
                    //TODO:秒杀核心代码
                    ItemKill itemKill = itemKillMapper.selectById(killId);

                    if (itemKill != null && itemKill.getCanKill() == 1){
                        //TODO:从这行开始加total>0 还有DCL
                        int res = itemKillMapper.updateKillItem(killId);
                        if (res > 0){
                            commonRecordKillSuccessInfo(itemKill,userId);
                            result = true;
                        }
                    }
                } catch (Exception e) {
                    log.info("userId: {}, 秒杀核心异常", userId);
                    throw new Exception("还没到抢购日期、已过了抢购时间或已被抢购完毕！");
                } finally {
                    // 判断和删除两个操作分开了，不是原子性的
                    if (val.equals(valueOperations.get(key))){
                        stringRedisTemplate.delete(key);
                    }
                }
            }
        }else {
            log.info("userId: {}, 您已经抢购过该商品", userId);
            throw new Exception("您已经抢购过该商品");
        }

        return result;
    }

    @Override
    public Boolean killItemRedissonLock(Integer killId, Integer userId) throws Exception {
        boolean result = false;

        if (itemKillSuccessMapper.selectByKillUser(killId, userId) == 0){

            //TODO:Redisson分布式锁
            final String key = killId.toString() + "-RedissonLock";

            RLock rLock = redissonClient.getLock(key);
            boolean cacheRes = false;

            cacheRes = rLock.tryLock(30, 10, TimeUnit.SECONDS);

            if (cacheRes){

                try {
                    //TODO:秒杀核心代码
                    ItemKill itemKill = itemKillMapper.selectById(killId);

                    if (itemKill != null && itemKill.getCanKill() == 1){
                        //TODO:从这行开始加total>0 还有DCL
                        int res = itemKillMapper.updateKillItem(killId);
                        if (res > 0){
                            commonRecordKillSuccessInfo(itemKill,userId);
                            result = true;
                        }
                    }
                } catch (Exception e) {
                    log.info("userId:{} --秒杀核心异常", userId);
                    throw new Exception("还没到抢购日期、已过了抢购时间或已被抢购完毕！");
                } finally {
                    rLock.unlock();
                }
            }
        }else {
            log.info("userId:{} --您已经抢购过该商品", userId);
            throw new Exception("您已经抢购过该商品");
        }
        return result;
    }

    private void commonRecordKillSuccessInfo(ItemKill kill, Integer userId) throws Exception{
        ItemKillSuccess entity = new ItemKillSuccess();

//        String orderCode = RandomUtil.generateOrderCode();

        String orderCode = String.valueOf(snowFlake.nextId());

        entity.setCode(orderCode);
        entity.setItemId(kill.getItemId());
        entity.setKillId(kill.getId());
        entity.setUserId(userId.toString());
        entity.setStatus(SysConstant.OrderStatus.SuccessNotPayed.getCode().byteValue());
        entity.setCreateTime(DateTime.now().toDate());

        int res = itemKillSuccessMapper.insertSelective(entity);
        if (res > 0){
            //TODO:异步消息通知
            log.info("订单创建成功-userId:{} killId:{}",userId,kill.getId());
        }
    }

    private void commonRecordKillSuccessInfoDCL(ItemKill kill, Integer userId) throws Exception{
        ItemKillSuccess entity = new ItemKillSuccess();

//        String orderCode = RandomUtil.generateOrderCode();

        SnowFlake snowFlake = new SnowFlake(2, 3);
        String orderCode = String.valueOf(snowFlake.nextId());

        entity.setCode(orderCode);
        entity.setItemId(kill.getItemId());
        entity.setKillId(kill.getId());
        entity.setUserId(userId.toString());
        entity.setStatus(SysConstant.OrderStatus.SuccessNotPayed.getCode().byteValue());
        entity.setCreateTime(DateTime.now().toDate());

        if (itemKillSuccessMapper.selectByKillUser(kill.getId(), userId) == 0){
            int res = itemKillSuccessMapper.insertSelective(entity);
            if (res > 0){
                //TODO:异步消息通知
                log.info("订单创建成功-userId:{} killId:{}",userId,kill.getId());
            }
        }
    }

}
