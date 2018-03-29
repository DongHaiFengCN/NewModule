package bean.kitchenmanage.order;

import bean.kitchenmanage.user.UsersC;

import java.util.ArrayList;
import java.util.List;

/**
 *  挂单类
 * <p>
 * Created by loongsun on 2017/12/4.
 * <p>
 * email: 125736964@qq.com
 */

public class OnOrderC {
    /**
     * 公司唯一身份id,用于数据同步,做为唯一管道符
     */
    private String channelId;
    /**
     * 类名，用于数据库查询类过滤
     */
    private String className;
    /**
     * docId
     */
    private String _id;

    /**
     * 包含的订单
     */
    private List<String> orderList;
    /**
     * 结账日期
     *  yyyy-MM-dd HH:mm:ss
     *  之所以定义成String型，是因为 DB4o 按该字段排序时不支持 Date型
     */
    private String onTime;
    /**
     * 所属桌位号
     */
    private String tableNum;

    /**
     *应收
     */
    private float needPay;

    /**
     * 操作员
     */
    private UsersC operator;

    /**
     * onName
     * 单位名称或姓名
     */
    private String onName;
    /**
     * onTel
     * 联系方式
     */
    private String onTel;
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

    public OnOrderC() {
    }

    public OnOrderC(String channelId) {

        this.channelId = channelId;
        this.className ="OnOrderC";
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

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public List<String> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<String> orderList) {
        this.orderList = orderList;
    }

    public String getOnTime() {
        return onTime;
    }

    public void setOnTime(String onTime) {
        this.onTime = onTime;
    }

    public String getTableNum() {
        return tableNum;
    }

    public void setTableNum(String tableNum) {
        this.tableNum = tableNum;
    }

    public float getNeedPay() {
        return needPay;
    }

    public void setNeedPay(float needPay) {
        this.needPay = needPay;
    }

    public UsersC getOperator() {
        return operator;
    }

    public void setOperator(UsersC operator) {
        this.operator = operator;
    }

    public String getOnName() {
        return onName;
    }

    public void setOnName(String onName) {
        this.onName = onName;
    }

    public String getOnTel() {
        return onTel;
    }

    public void setOnTel(String onTel) {
        this.onTel = onTel;
    }
    public void addOrder(String obj)
    {
        if(orderList==null)
            orderList=new ArrayList<>();
        orderList.add(obj);
    }
}
