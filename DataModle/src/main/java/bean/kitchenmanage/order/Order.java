package bean.kitchenmanage.order;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * @ClassName: Order
 * @Description: 订单类
 * @author loongsun
 * @date 2014-7-28 下午11:44:16
 *
 */
public class Order {
	/**
	 * docId
	 */
	private String id;
	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className = "Order";
	/**
	 * 数据分两大类，一个是基础数据 BaseData，一个业务实时数据 UserData
	 */
	private String dataType = "UserData";
	/**
	 * 只有业务数据才有年份划分:2018
	 */
	private String createdYear;
	/**
	 * 每天的流水号，从001开始，按序向后排。按营业时间重新产生序号。
	 */
	private String serialNum;
	/**
	 * 订单序号，只做同桌多次点餐时使用,第一次下单为序号为1，第二次下单序号为2
	 */
	private int orderNum;
	/**
	 *打印标志flag为0,未打印；1，代表本地打印完毕；2,代表厨房打印机打印，3，代表两者都已打印
	 */
	private int printFlag;
	/**
	 * 订单金额
	 */
	private float totalPrice;
	/**
	 * 订单金额会员价
	 */
	private float totalMemberPrice;
	/**
	 *订单产生时间
	 *yyyy-MM-dd HH:mm:ss
	 */
	private String createdTime;
	/**
	 *排队号，保留，为排队人下的单
	 */
	private String queueNum;
	/**
	 *订单状态 0：已买单；1:刚下单未买单；2：消台订单；
	 */
	private int state;
	/**
	 *0,前台主程序所下订单;1、点餐宝所下订单;2、手机所下订单
	 */
	private int deviceType;
	/**
	 *退菜标识 0.正常  1.退菜，2 赠菜
	 */
	private int orderType;
	/**
	 * 所点商品列表
	 */
	private List<Goods> goodsList;
	/**
	 * 桌位Id
	 */
	private String tableId;
	/**
	 * 操作员员工号
	 */
	private String employeeNum;
	/**
	 *桌位状态记录对象
	 */
	private String  tableLogId;
	/**
	 *忌口信息
	 */
	private List<String> taboosId;
	/**
	 * 下单手机的mac地址
	 */
	private String macAddress;
	/**
	 * 微信用户：open_id;预订者用户：手机号码；会员用户：手机号码
	 */
	private String openId;
	/**
	 * 订单备注
	 */
	private String description;
	/**
	 * 消台原因
	 */
	private String cancelReason;

	/**
	 * 消台时间
	 */
	private String cancelTime;

	public Order() {
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

	public String getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}

	public int getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}

	public int getPrintFlag() {
		return printFlag;
	}

	public void setPrintFlag(int printFlag) {
		this.printFlag = printFlag;
	}

	public float getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}

	public float getTotalMemberPrice() {
		return totalMemberPrice;
	}

	public void setTotalMemberPrice(float totalMemberPrice) {
		this.totalMemberPrice = totalMemberPrice;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public String getQueueNum() {
		return queueNum;
	}

	public void setQueueNum(String queueNum) {
		this.queueNum = queueNum;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	public int getOrderType() {
		return orderType;
	}

	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}

	public List<Goods> getGoodsList() {
		return goodsList;
	}

	public void setGoodsList(List<Goods> goodsList) {
		this.goodsList = goodsList;
	}


	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public String getEmployeeNum() {
		return employeeNum;
	}

	public void setEmployeeNum(String employeeNum) {
		this.employeeNum = employeeNum;
	}

	public String getTableLogId() {
		return tableLogId;
	}

	public void setTableLogId(String tableLogId) {
		this.tableLogId = tableLogId;
	}

	public List<String> getTaboosId() {
		return taboosId;
	}

	public void setTaboosId(List<String> taboosId) {
		this.taboosId = taboosId;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	public String getCancelTime() {
		return cancelTime;
	}

	public void setCancelTime(String cancelTime) {
		this.cancelTime = cancelTime;
	}
}
