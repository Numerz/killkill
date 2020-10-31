package cn.wxn.killkill.server.service;

import cn.wxn.killkill.model.entities.ItemKill;

import java.util.List;

public interface ItemService {
    List<ItemKill> showList() throws Exception;

    ItemKill getKillDetail(Integer id) throws Exception;
}
