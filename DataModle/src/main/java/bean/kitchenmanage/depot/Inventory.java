package bean.kitchenmanage.depot;


import java.io.Serializable;
import java.util.ArrayList;

import bean.kitchenmanage.user.UsersC;


/**
 * @ClassName: Inventory
 * @Description: 盘点类
 * @author loongsun
 * @date 2016-01-01 上午1:19:08
 *
 */
public class Inventory implements Serializable
{

    /**
     * 公司唯一身份id,用于数据同步,做为唯一管道符
     */
    private String channelId;
    /**
     * 盘点编号
     */
    private String id;
    /**
     * 创建日期
     */
    private String time;
    /**
     * 盘点人
     */
    private String operator;
    /**
     * 备注
     */
    private String note;
    /**
     * 状态 1、草稿；2、提交
     */
    private int state;
    /**
     * 每个源料的盘点记录
     */
    private ArrayList<String> inventoryItemArrayList;

    public Inventory()
    {
    }

    public Inventory(String id, String time, UsersC operator, String note, int state) {
        this.id = id;
        this.time = time;

        this.note = note;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }



    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

}
