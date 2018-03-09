package bean.kitchenmanage.order;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bean.kitchenmanage.user.UsersC;


/**
 * @ClassName: Order
 * @Description: 订单类
 * @author loongsun
 * @date 2014-7-28 下午11:44:16
 *
 */
public class OrderC implements Serializable{
    //1、
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
	 * 订单序号，只做同桌多次点餐时使用,第一次下单为序号为1，第二次下单序号为2
	 */
	private int orderNum;
	/**
	 * 每天的流水号，从001开始，按序向后排。按营业时间重新产生序号。
	 */
	private String serialNum;
	/**
	 *打印标志flag为0,未打印，1，代表本地打印完毕,2,代表厨房打印机打印，3，代表两者都已打印
	 */
	private int printFlag;
	/**
	 * 订单金额
	 */
	private float allPrice;//
	/**
	 *订单产生时间
	 *yyyy-MM-dd HH:mm:ss
	 */
	private String createdTime;// 日期时间****
	/**
	 *排队号，保留
	 */
	private String queueNum;//排队号
	/**
	 * 下单手机的mac地址
	 */
	private String mac;
	/**
	 *订单状态 0：已买单；1:刚下单未买单；2：消台订单；
	 */
	private int orderState;
	/**
	 *0,前台主程序所下订单;1、点餐宝所下订单;2、手机所下订单
	 */
	private int deviceType;
	/**
	 *退菜标识 0.正常  1.退菜，2 赠菜
	 */
	private int orderCType;
	/**
	 * 所点商品列表
	 */
	private List<GoodsC> goodsList;
	/**
	 * 网点名称
	 */
	private String comapnyName;//网点名称
	/**
	 * 区域名称
	 */
	private String areaName;
	/**
	 * 桌位名称
	 */
	private String tableName;
	/**
	 *桌位号
	 */
	private String tableNo;
	/**
	 * 操作员员工号
	 */
	private String operatorNum;
	/**
	 *桌位状态记录对象
	 */
	private String  tableLogId;
	/**
	 * 订单备注
	 */
    private String note;
	/**
	 *忌口信息
	 */
	private List<String> taboosId;
	/**
	 * 微信用户：open_id;预订者用户：手机号码；会员用户：手机号码
	 */
	private String userId;

	/**
	 * 只有业务数据才有年份划分:2018
	 */
	private String createdYear;
	/**
	 * 数据分两大类，一个是基础数据 basic，一个业务实时数据 business
	 */
	private String dataType = "UserData";
	/**
	 * 订单备注
	 */
	private String desc;
	/**
	 * 消台原因
	 */
	private String cancelReason;

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



	public OrderC()
	{
	}

	public OrderC(String company_id) {
		this.channelId = company_id;
		this.className="OrderC";
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
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
	public int getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}

	public String getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}

	public int getPrintFlag() {
		return printFlag;
	}

	public void setPrintFlag(int printFlag) {
		this.printFlag = printFlag;
	}

	public float getAllPrice() {
		return allPrice;
	}

	public void setAllPrice(float allPrice) {
		this.allPrice = allPrice;
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

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public int getOrderState() {
		return orderState;
	}

	public void setOrderState(int orderState) {
		this.orderState = orderState;
	}

	public List<GoodsC> getGoodsList() {
		return goodsList;
	}

	public void setGoodsList(List<GoodsC> goodsList) {
		this.goodsList = goodsList;
	}

	public void addGoods(GoodsC obj)
	{
		if(this.goodsList==null)
			this.goodsList=new ArrayList<GoodsC>();
		this.goodsList.add(obj);
	}
	public String getComapnyName() {
		return comapnyName;
	}

	public void setComapnyName(String comapnyName) {
		this.comapnyName = comapnyName;
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

	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	public int getOrderCType() {
		return orderCType;
	}

	public void setOrderCType(int orderCType) {
		this.orderCType = orderCType;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOperatorNum() {
		return operatorNum;
	}

	public void setOperatorNum(String operatorNum) {
		this.operatorNum = operatorNum;
	}

	public List<String> getTaboosId() {
		return taboosId;
	}

	public void setTaboosId(List<String> taboosId) {
		this.taboosId = taboosId;
	}

	public String getTableLogId() {
		return tableLogId;
	}

	public void setTableLogId(String tableLogId) {
		this.tableLogId = tableLogId;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}
}
