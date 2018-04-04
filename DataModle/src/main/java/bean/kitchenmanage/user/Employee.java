/**   
* @Title: Workers.java 
* @Package com.canyin.db4o.renyuan 
* @Description
* @author loongsun 
* @date 2014-7-29 上午1:19:08 
* @version V1.0   
*/
package bean.kitchenmanage.user;


import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: Users
 * @Description: 员工类文件
 * @author loongsun
 * @date 2014-7-29 上午1:19:08 
 *  
 */
public class Employee {
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
	private String className = "Employee";
	/**
	 * 数据分两大类，一个是基础数据 basic，一个业务实时数据 business
	 */
	private String dataType = "BaseData";
	/**
	 * 员工号,唯一判断
	 */
	private String userName;
	/**
	 * 员工密码
	 */
	private String pwd;
	/**
	 *员工姓名
	 */
	private String name;
	/**
	 *员工生日
	 */
	private String birthday;
	/**
	 *性别
	 */
	private String sex;//性别
	/**
	 *入职时间;格式：2014-01-01
	 */
	private String enterTime;
	/**
	 *离职时间;格式2014-01-01
	 */
	private String leaveTime;//离职时间;2014-01-01
	/**
	 * 注册时间
	 */
	private String registedTime;
	/**
	 *工作岗位,可以在多个岗位任职
	 */
	private List<String> stationIds;

	/**
	 *联系电话
	 */
	private String mobile;
	/**
	 *员工状态，0：正常；1：禁用；2：离岗
	 */
	private int state;

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}



	public Employee() {
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getEnterTime() {
		return enterTime;
	}

	public void setEnterTime(String enterTime) {
		this.enterTime = enterTime;
	}

	public String getLeaveTime() {
		return leaveTime;
	}

	public void setLeaveTime(String leaveTime) {
		this.leaveTime = leaveTime;
	}

	public String getRegistedTime() {
		return registedTime;
	}

	public void setRegistedTime(String registedTime) {
		this.registedTime = registedTime;
	}

	public List<String> getStationIds() {
		return stationIds;
	}

	public void setStationIds(List<String> stationIds) {
		this.stationIds = stationIds;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
