package bean.kitchenmanage.depot;


import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.List;

import bean.kitchenmanage.user.UsersC;

/**
 * @ClassName: MaterialDelive
 * @Description: 源料出库类文件
 * @author loongsun
 * @date 2016-01-01 上午1:19:08
 *
 */
public class MaterialDelive implements Serializable


{

    /**
     * 公司唯一身份id,用于数据同步,做为唯一管道符
     */
    private String channelId;
    /**
     * 出库单号
     */
    private String id;
    /**
     * 出库时间
     */
    private String time;//
    /**
     * 系统操作人
     */
    private String operator;
    /**
     * 源料申请人
     */
    private String requester;//申请人
    /**
     * 出库总价值
     */
    private float totalValue;//
    /**
     * 备注
     */
    private String note;//
    /**
     * 出库记录状态 1、草稿，2、提交
     */
    private int state;
    /**
     * 出库源料列表
     */
    private Array materialDeliveItemList;

    public MaterialDelive() {
    }

    public MaterialDelive(String id, String time, UsersC requester, UsersC operator, float totalValue, String note, int state, List<MaterialDeliveItem> materialDeliveItemList) {
        this.id = id;
        this.totalValue = totalValue;
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







    public float getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(float totalValue) {
        this.totalValue = totalValue;
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
