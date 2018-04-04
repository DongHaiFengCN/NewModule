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
 * @ClassName:
 * @Description: 网点类，终端不允许创建此类对象，由接口admin权限创建
 * @author loongsun
 * @date 2014-7-29 上午1:06:02 
 *  
 */
public class Company {

	/**
	 * 对象id,等于docmentid,一般用于Pojo操作时使用。
	 */
	private String id;
	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * 父机构channelId
	 */
	private String parentChannelId;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className = "Company";
	/**
	 * 数据分两大类，一个是基础数据 BaseData，一个业务实时数据 UserData
	 */
	private String dataType = "BaseData";
	/**
	 *网点名称
	 */
	private String name;
	/**
	 *网点地址
	 */
	private String address;

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
	 * channel通道同步授权密码
	 */
	private String pwd;
	/**
	 * 子机构
	 */
	private List<String> subCompanyIds;

	public Company() {
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

	public String getParentChannelId() {
		return parentChannelId;
	}

	public void setParentChannelId(String parentChannelId) {
		this.parentChannelId = parentChannelId;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getLinkman() {
		return linkman;
	}

	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public List<String> getSubCompanyIds() {
		return subCompanyIds;
	}

	public void setSubCompanyIds(List<String> subCompanyIds) {
		this.subCompanyIds = subCompanyIds;
	}
}
