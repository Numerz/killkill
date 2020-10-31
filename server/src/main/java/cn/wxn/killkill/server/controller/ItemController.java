package cn.wxn.killkill.server.controller;

import cn.wxn.killkill.model.entities.ItemKill;
import cn.wxn.killkill.server.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class ItemController {

    private static final Logger log = LoggerFactory.getLogger(ItemController.class);

    private static final String prefix = "/item";

    private ItemService itemService;

    public ItemService getItemService() {
        return itemService;
    }

    @Autowired
    public void setItemService(ItemService itemService) {
        this.itemService = itemService;
    }

    @RequestMapping(value = {"/", "/index", "/index.html", prefix + "list"}, method = RequestMethod.GET)
    public String showItemKillList(ModelMap modelMap){
        try {
            List<ItemKill> itemKills = itemService.showList();
            modelMap.put("list", itemKills);

            log.info("待秒杀商品列表-数据{}", itemKills);
        } catch (Exception e) {
            log.error("获取待秒杀商品列表-发生异常:", e.fillInStackTrace());
            return "redirect:/base/error";
        }
        return "list";
    }

    @RequestMapping(value = prefix + "/detail/{id}", method = RequestMethod.GET)
    public String detail(@PathVariable Integer id, ModelMap modelMap){
        if (id == null || id <= 0){
            return "redirect:/base/error";
        }

        try {
            ItemKill itemKill = itemService.getKillDetail(id);
            modelMap.put("detail", itemKill);
        } catch (Exception e) {
            log.error("获取待秒杀详情发生异常：id = {}", id, e.fillInStackTrace());
            return "redirect:/base/error";
        }
        return "info";
    }
}
