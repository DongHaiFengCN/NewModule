/**   
* @Title: Stations.java
* @Package com.kitchenmanage.userM
* @Description: 岗位类文件
* @author loongsun 
* @date 2014-7-29 上午1:02:42 
* @version V1.0   
*/
package bean.kitchenmanage.user;

public class StationsC {
	private String _id;

	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className;
	/**
	 *员工角色 1对多
	 */
	private RoleModelC rolemodel;
	/**
	 *岗位名称
	 */
	private String stationName;
	/**
	 *所属部门
	 */
	private String departmentId;
	/**
	 *创建时间
	 */
	private String createdTime;
	/**
	 *是否有效
	 */
	private boolean valid;
	/**
	 *岗位仅一个用户
	 */
	private  String userId;

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

	public StationsC() {
	}

	public StationsC(String channelId) {
		this.channelId = channelId;
		this.className="StationsC";
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

	public RoleModelC getRolemodel() {
		return rolemodel;
	}

	public void setRolemodel(RoleModelC rolemodel) {
		this.rolemodel = rolemodel;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	//	public String getUsersId() {
//		return userId;
//	}
//
//	public void setUsersId(String usersId) {
//		this.userId = usersId;
//	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
