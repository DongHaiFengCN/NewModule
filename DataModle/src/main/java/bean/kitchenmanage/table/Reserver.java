package bean.kitchenmanage.table;

import java.util.Date;
import java.util.List;

/**
 * Class Reserve,预约人员信息
 * <p>
 * Created by loongsun on 17/1/6.
 * <p>
 * email: 125736964@qq.com
 */

public class Reserver {
    /**
     * 对象id,等于docmentid,一般用于Pojo操作时使用。
     */
    private String id;
    /**
     * 公司唯一身份id,用于数据同步,做为唯一管道符
     */
    private String channelId;
    /**
     * 类名，用于数据库查询类过滤
     */
    private String className;

    /**
     * 数据分两大类，一个是基础数据 BaseData，一个业务实时数据 UserData
     */
    private String dataType = "UserData";
    /**
     * 只有业务数据才有年份划分:2018
     */
    private String createdYear;
    /**
     * 预定人姓名
     */
    private String name;
    /**
     * 预定人电话
     */
    private String mobile;
    /**
     * 预订产生时间
     */
    private String createdTime;
    /**
     * 就餐时间
     */
    private String reserveTime;
    /**
     * 就餐人数
     */
    private int personNum;
    /**
     *  桌位ids
     */
    private List<String> tableIds;
    /**
     * 0,预定；1，未履约；2，履约
     */
    private int stateFlag;
    /**
     * 备注
     */
    private String note;

    public Reserver() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getCreatedYear() {
        return createdYear;
    }

    public void setCreatedYear(String createdYear) {
        this.createdYear = createdYear;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getReserveTime() {
        return reserveTime;
    }

    public void setReserveTime(String reserveTime) {
        this.reserveTime = reserveTime;
    }

    public int getPersonNum() {
        return personNum;
    }

    public void setPersonNum(int personNum) {
        this.personNum = personNum;
    }

    public List<String> getTableIds() {
        return tableIds;
    }

    public void setTableIds(List<String> tableIds) {
        this.tableIds = tableIds;
    }

    public int getStateFlag() {
        return stateFlag;
    }

    public void setStateFlag(int stateFlag) {
        this.stateFlag = stateFlag;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

