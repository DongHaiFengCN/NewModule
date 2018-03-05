package bean.kitchenmanage.table;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bean.kitchenmanage.user.UsersC;

/**
 * @ClassName: Tables
 * @Description: 桌位类文件
 * @author loongsun
 * @date 2017-01-05
 *
 */
public class TableC implements Serializable{
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
	 *所属房间父对象
	 */
	private String areaId;

	/**
	 *桌位序号  001，三位数
	 */
	private String tableNum;
	/**
	 *桌位名称
	 */
	private String tableName;
	/**
	 *容纳最大人数
	 */
	private int maxPersons;
	/**
	 * 允许最少人数
	 */
	private int minPersions;
	/**
	 *当前人数(针对正在使用的)
	 */
	private int currentPersions;
	/**
	 *最低消费金额
	 */
	private int minConsum;

	/**
	 *位状态  ：0,空闲;1,预定，2,使用;
	 */
	private int state;
	/**
	 *是否有效. 0,无效；1，代表有效
	 */
	//private boolean isValid;
	private  boolean valid;
	/**
	 * 在人员分配管理桌位时，可进行关联
	 */
	private List<UsersC> usersList;



	public String getLastCheckOrderId() {
		return lastCheckOrderId;
	}

	public void setLastCheckOrderId(String lastCheckOrderId) {
		this.lastCheckOrderId = lastCheckOrderId;
	}

	/**
	 * 记录最近一次checkorder订单id
	 */

	private String lastCheckOrderId;

	/**
	 * 数据分两大类，一个是基础数据 basic，一个业务实时数据 business
	 */
	private String dataType = "BaseData";//basi,代表basic数据,busi,代表业务数据

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}



	public TableC()
	{
	}

	public TableC(String company_id)
	{
		this.channelId = company_id;
		this.className="TableC";
	}



	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}



	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public String getTableNum() {
		return tableNum;
	}

	public void setTableNum(String tableNum) {
		this.tableNum = tableNum;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getMaxPersons() {
		return maxPersons;
	}

	public void setMaxPersons(int maxPersons) {
		this.maxPersons = maxPersons;
	}

	public int getMinPersions() {
		return minPersions;
	}

	public void setMinPersions(int minPersions) {
		this.minPersions = minPersions;
	}

	public int getCurrentPersions() {
		return currentPersions;
	}

	public void setCurrentPersions(int currentPersions) {
		this.currentPersions = currentPersions;
	}

	public int getMinConsum() {
		return minConsum;
	}

	public void setMinConsum(int minConsum) {
		this.minConsum = minConsum;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
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

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean is) {
		valid = is;
	}


	public List<UsersC> getUsersList()
	{
		if(usersList==null)
			this.usersList=new ArrayList<>();
		return usersList;
	}

	public void setUsersList(List<UsersC> usersList) {
		this.usersList = usersList;
	}
	public void addUsersC(UsersC obj)
	{
		if(this.usersList==null)
			this.usersList=new ArrayList<>();
		this.usersList.add(obj);
	}
}
