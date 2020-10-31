package cn.wxn.killkill.server.service.impl;

import cn.wxn.killkill.model.entities.ItemKill;
import cn.wxn.killkill.model.mapper.ItemKillMapper;
import cn.wxn.killkill.server.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemService.class);

    private ItemKillMapper itemKillMapper;

    public ItemKillMapper getItemKillMapper() {
        return itemKillMapper;
    }

    @Autowired
    public void setItemKillMapper(ItemKillMapper itemKillMapper) {
        this.itemKillMapper = itemKillMapper;
    }

    @Override
    public List<ItemKill> showList() throws Exception {
        return itemKillMapper.selectAll();
    }

    @Override
    public ItemKill getKillDetail(Integer id) throws Exception {
        ItemKill itemKill = itemKillMapper.selectById(id);
        if (itemKill == null){
            throw new Exception("获取待秒杀详情不存在");
        }
        return itemKill;
    }
}
