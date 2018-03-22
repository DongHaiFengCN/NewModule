package bean.kitchenmanage.member;


import java.util.Date;

import bean.kitchenmanage.user.UsersC;

/**
 * @ClassName: CancelCardLog
 * @Description: 注销会员卡
 * @author loongsun
 * @date 2014-7-29 上午1:06:02
 *
 */
public class CancelCardLogC {

    /**
     * 公司唯一身份id,用于数据同步,做为唯一管道符
     */
    private String channelId;
    /**
     * 类名，用于数据库查询类过滤
     */
    private String className;
    /**
     *
     * docId
     */
    private String _id;


    /**
     * 消卡的会员信息
     */
    private String  membersId;
    /**
     *退款金额
     */
    private float refund;
    /**
     * 上次余额
     */
    private String remainder;
    /**
     *操作者
     */
    private UsersC operator;
    /**
     *操作日期
     */
    private Date time;

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

    public CancelCardLogC() {
    }

    public CancelCardLogC(String company_id) {
        this.channelId = company_id;
        this.className="CancelCardLogC";
    }

    public String getRemainder() {
        return remainder;
    }

    public void setRemainder(String remainder) {
        this.remainder = remainder;
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

    public String getMembersId() {
        return membersId;
    }

    public void setMembersId(String membersId) {
        this.membersId = membersId;
    }

    public float getRefund() {
        return refund;
    }

    public void setRefund(float refund) {
        this.refund = refund;
    }

    public UsersC getOperator() {
        return operator;
    }

    public void setOperator(UsersC operator) {
        this.operator = operator;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
