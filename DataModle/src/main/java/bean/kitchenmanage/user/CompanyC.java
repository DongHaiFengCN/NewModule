/**   
* @Title: Point.java 
* @Package com.canyin.db4o.renyuan 
* @Description: TODO(用一句话描述该文件做什么) 
* @author loongsun 
* @date 2014-7-29 上午1:06:02 
* @version V1.0   
*/
package bean.kitchenmanage.user;

import java.util.List;

/**
 * @ClassName: Point 
 * @Description: 公司类文件
 * @author loongsun
 * @date 2014-7-29 上午1:06:02 
 *  
 */
public class CompanyC {


	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className;
	/**
	 * 对象id,等于docmentid,一般用于Pojo操作时使用。
	 */
	private String _id;

	/**
	 *网点名称
	 */
	private String pointName;
	/**
	 *网点地址
	 */
	private String pointAddress;
	/**
	 * 百度地图坐标，经度
	 */
	private double  x;
	/**
	 * 百度地图坐标，纬度
	 */
	private double  y;
	/**
	 *商户联系电话,可以是固定电话
	 */
	private String telephone;
	/**
	 * 用户绑定手机号，用于登录
	 */
	private String mobile;
	/**
	 *联系人
	 */
	private String linkman;
	/**
	 *是否有效
	 */
	private boolean valid;
	/**
	 * 包含的多个部门对象
	 */
	private List<String> deplist;
	/**
	 * 营业启始时间  格式为  HH:mm:ss
	 */
	private String businessStartHours;
	/**
	 * 营业结束时间  格式为  HH:mm:ss
	 */
	private String businessEndHours;
	/**
	 * 父机构channelId
	 */
	private String parentChannelId;
	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * channel通道同步授权密码
	 */
	private String password;
	/**
	 * 子机构
	 */
	private List<CompanyC> subChannelIdList;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public CompanyC() {
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

	public String getPointName() {
		return pointName;
	}

	public void setPointName(String pointName) {
		this.pointName = pointName;
	}

	public String getPointAddress() {
		return pointAddress;
	}

	public void setPointAddress(String pointAddress) {
		this.pointAddress = pointAddress;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getLinkman() {
		return linkman;
	}

	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}



	public List<String> getDeplist() {
		return deplist;
	}

	public void setDeplist(List<String> deplist) {
		this.deplist = deplist;
	}

	public String getBusinessStartHours() {
		return businessStartHours;
	}

	public void setBusinessStartHours(String businessStartHours) {
		this.businessStartHours = businessStartHours;
	}

	public String getBusinessEndHours() {
		return businessEndHours;
	}

	public void setBusinessEndHours(String businessEndHours) {
		this.businessEndHours = businessEndHours;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public String getParentChannelId() {
		return parentChannelId;
	}

	public void setParentChannelId(String parentChannelId) {
		this.parentChannelId = parentChannelId;
	}

	public List<CompanyC> getSubChannelIdList() {
		return subChannelIdList;
	}

	public void setSubChannelIdList(List<CompanyC> subChannelIdList) {
		this.subChannelIdList = subChannelIdList;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
}
