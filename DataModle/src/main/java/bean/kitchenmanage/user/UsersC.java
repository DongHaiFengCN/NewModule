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
public class UsersC {
	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className;
	/**
	 * 员工号
	 */
	private String userName;
	/**
	 * 登陆密码
	 */
	//private String password;
	private String passwd;
	/**
	 *员工姓名
	 */
	private String employeeName;
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
	private List<String> stationsId;
	/**
	 * 终端程序用户权限 1级：全部功能；2级：点餐、收银；3级：库管
	 */
	private int  level;
	/**
	 *联系电话
	 */
	private String mobile;
	/**
	 *员工状态，1：正常；2：注销；3：其它
	 */
	private int state;//
	/**
	 *
	 */
	private String _id;


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



	public UsersC() {
	}

	public UsersC(String channelId) {
		this.channelId = channelId;
		this.className ="UsersC";
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

//	public String getPassword() {
//		return password;
//	}
//
//	public void setPassword(String password) {
//		this.password = password;
//	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
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

	public List<String> getStationsId() {
		return stationsId;
	}

	public void setStationsId(List<String> stationsId) {
		this.stationsId = stationsId;
	}

	public void addStationsId(String stationId)
	{
		if(this.stationsId==null)
			this.stationsId = new ArrayList<>();

		this.stationsId.add(stationId);
	}
	public void removeStationId(String stationId)
	{
		if(stationsId!=null)
			stationsId.remove(stationId);
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
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

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}
}
