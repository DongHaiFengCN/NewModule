/**   
* @Title: Stations.java
* @Package com.kitchenmanage.userM
* @Description: 岗位类文件
* @author loongsun 
* @date 2014-7-29 上午1:02:42 
* @version V1.0   
*/
package bean.kitchenmanage.user;

import java.util.List;

public class Station {

	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String id;

	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className = "Station";
	/**
	 * 数据分两大类，一个是基础数据 BaseData，一个业务实时数据 UserData
	 */
	private String dataType = "BaseData";
	/**
	 *所属部门
	 */
	private String departmentId;
	/**
	 *岗位名称
	 */
	private String name;
	/**
	 *创建时间
	 */
	private String createdTime;
	/**
	 *是否有效
	 */
	private boolean valid;
	/**
	 *员工ids
	 */
	private List<String> userIds;
	/**
	 *对应角色
	 */
	private String roleId;

	public Station() {
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

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public List<String> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
}
