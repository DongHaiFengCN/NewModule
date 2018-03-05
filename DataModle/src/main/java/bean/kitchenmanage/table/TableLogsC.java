package bean.kitchenmanage.table;



import java.io.Serializable;
import java.util.Date;

public class TableLogsC implements Serializable{
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
	 * 对象id,等于docmentid,一般用于Pojo操作时使用。
	 */
	private String _id;
	/**
	 *桌位号
	 */
	private String tableNum;
	/**
	 *订单号
	 */
	private String orderID;
	/**
	 * 预定时间
	 */
	private Date reserveTime;
	/**
	 * 开台使用时间
	 */
	private Date usingTime;
	/**
	 * 结账时间
	 */
	private Date checkTime;
	/**
	 * 消台时间
	 */
	private Date fireTime;
	/**
	 *就餐人数
	 */
	private int personNum;//
	/**
	 * 备注
	 */
	private  String note;
	/**
	 * 桌位状态标志；1、预定；2，开台。3，结帐，4，消台
	 */
	private  int operateFlag;
	/**
	 *操作者对象
	 */
	private String operatorName;


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

	public TableLogsC() {
	}

	public TableLogsC(String company_id) {
		this.channelId = company_id;
		this.className="TableLogsC";
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

	public String getTableNum() {
		return tableNum;
	}

	public void setTableNum(String tableNum) {
		this.tableNum = tableNum;
	}

	public String getOrderID() {
		return orderID;
	}

	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}

	public Date getReserveTime() {
		return reserveTime;
	}

	public void setReserveTime(Date reserveTime) {
		this.reserveTime = reserveTime;
	}

	public Date getUsingTime() {
		return usingTime;
	}

	public void setUsingTime(Date usingTime) {
		this.usingTime = usingTime;
	}

	public Date getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(Date checkTime) {
		this.checkTime = checkTime;
	}

	public Date getFireTime() {
		return fireTime;
	}

	public void setFireTime(Date fireTime) {
		this.fireTime = fireTime;
	}

	public int getPersonNum() {
		return personNum;
	}

	public void setPersonNum(int personNum) {
		this.personNum = personNum;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public int getOperateFlag() {
		return operateFlag;
	}

	public void setOperateFlag(int operateFlag) {
		this.operateFlag = operateFlag;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
}
