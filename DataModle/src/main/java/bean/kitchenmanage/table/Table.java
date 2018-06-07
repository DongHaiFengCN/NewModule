package bean.kitchenmanage.table;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: Tables
 * @Description: 桌位类文件
 * @author loongsun
 * @date 2017-01-05
 *
 */
public class Table implements Serializable{
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
	private String className = "Table";
	/**
	 * 数据分两大类，一个是基础数据 BaseData，一个业务实时数据 UserData
	 */
	private String dataType = "BaseData";
	/**
	 *所属区域id
	 */
	private String areaId;
	/**
	 *桌位名称
	 */
	private String name;
	/**
	 *桌位序号  001，三位数
	 */
	private int num;

	/**
	 *容纳最大人数
	 */
	private int maxPersons;
	/**
	 * 允许最少人数
	 */
	private int minPersons;
	/**
	 *当前人数(针对正在使用的)
	 */
	private int currentPersons;
	/**
	 *最低消费金额
	 */
	private int minConsum;
	/**
	 *位状态  ：0,空闲;1,预定，2,使用,4,无效;
	 */
	private int state;
	/**
	 * 在人员分配管理桌位时，可进行关联
	 */
	private List<String> userIds;
	/**
	 * 记录最近一次checkorder订单id
	 */
	private String lastCheckOrderId;

	public Table()
	{
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

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getMaxPersons() {
		return maxPersons;
	}

	public void setMaxPersons(int maxPersons) {
		this.maxPersons = maxPersons;
	}

	public int getMinPersons() {
		return minPersons;
	}

	public void setMinPersons(int minPersons) {
		this.minPersons = minPersons;
	}

	public int getCurrentPersons() {
		return currentPersons;
	}

	public void setCurrentPersons(int currentPersons) {
		this.currentPersons = currentPersons;
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

	public List<String> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}

	public String getLastCheckOrderId() {
		return lastCheckOrderId;
	}

	public void setLastCheckOrderId(String lastCheckOrderId) {
		this.lastCheckOrderId = lastCheckOrderId;
	}
}
