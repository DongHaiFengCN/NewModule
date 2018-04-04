package bean.kitchenmanage.member;


import java.util.Date;

import bean.kitchenmanage.user.Employee;

/**
 * @ClassName: CancelCardLog
 * @Description: 注销会员卡
 * @author loongsun
 * @date 2014-7-29 上午1:06:02
 *
 */
public class CancelCardLog {

    /**
     * 公司唯一身份id,用于数据同步,做为唯一管道符
     */
    private String channelId;
    /**
     * 类名，用于数据库查询类过滤
     */
    private String className = "CancelCardLog";
    /**
     *
     * docId
     */
    private String id;
    /**
     * 数据分两大类，一个是基础数据 BaseData，一个业务实时数据 UserData
     */
    private String dataType = "UserData";
    /**
     * 只有业务数据才有年份划分:2018
     */
    private String createdYear;
    /**
     * 消卡的会员docId
     */
    private String  memberId;
    /**
     *退款金额
     */
    private float refund;
    /**
     * 上次余额
     */
    private float remainder;
    /**
     *操作者
     */
    private Employee operator;
    /**
     *操作日期
     */
    private String createdTime;

    public CancelCardLog() {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public float getRefund() {
        return refund;
    }

    public void setRefund(float refund) {
        this.refund = refund;
    }

    public float getRemainder() {
        return remainder;
    }

    public void setRemainder(float remainder) {
        this.remainder = remainder;
    }

    public Employee getOperator() {
        return operator;
    }

    public void setOperator(Employee operator) {
        this.operator = operator;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }
}
