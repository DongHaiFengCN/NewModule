package bean.kitchenmanage.table;

import java.util.Date;

/**
 * Class Reserve,预约人员信息信息
 * <p>
 * Created by loongsun on 17/1/6.
 * <p>
 * email: 125736964@qq.com
 */

public class ReserverC {
    /**
     * 公司唯一身份id,用于数据同步,做为唯一管道符
     */
    private String channelId;
    /**
     * 类名，用于数据库查询类过滤
     */
    private String className;
    /**
     * 对象id,等于docmentid,一般用于Pojo操作时使用。
     */
    private String _id;
    /**
     * 预定人姓名
     */
    private String contacterName;
    /**
     * 预定人电话
     */
    private String contacterTel;// 联系电话
    /**
     * 就餐时间
     */
    private Date reserveTime;
    /**
     * 就餐人数
     */
    private int personNum;//
    /**
     * 预订产生时间
     */
    private Date createdTime;
    /**
     *  桌位号
     */
    private  String tableNo;

    /**
     * 1,预定；2，未履约；3，履约
     */
    private int stateFlag;
    /**
     * 备注
     */
    private String note;

    /**
     * 只有业务数据才有年份划分:2018
     */
    private String createdYear;
    /**
     * 数据分两大类，一个是基础数据 basic，一个业务实时数据 business
     */
    private String dataType = "UserData";

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


    public ReserverC() {
    }

    public ReserverC(String company_id) {
        this.channelId = company_id;
        this.className="ReserverC";
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public String getContacterName() {
        return contacterName;
    }

    public void setContacterName(String contacterName) {
        this.contacterName = contacterName;
    }

    public String getContacterTel() {
        return contacterTel;
    }

    public void setContacterTel(String contacterTel) {
        this.contacterTel = contacterTel;
    }

    public Date getReserveTime() {
        return reserveTime;
    }

    public void setReserveTime(Date reserveTime) {
        this.reserveTime = reserveTime;
    }

    public int getPersonNum() {
        return personNum;
    }

    public void setPersonNum(int personNum) {
        this.personNum = personNum;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
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

    public String getTableNo() {
        return tableNo;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }
}

