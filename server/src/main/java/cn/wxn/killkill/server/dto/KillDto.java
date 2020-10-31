package cn.wxn.killkill.server.dto;

import org.apache.ibatis.annotations.Param;

public class KillDto {

    private Integer killId;
    private Integer userId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getKillId() {
        return killId;
    }

    public void setKillId(Integer killId) {
        this.killId = killId;
    }
}
