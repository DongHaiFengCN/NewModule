package bean.kitchenmanage.member;
/**
 * @ClassName: Members
 * @Description: 会员类文件
 * @author loongsun
 * @date 2017-01-06
 *
 */

import java.util.List;
import bean.kitchenmanage.user.Employee;

public class Members {
	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className = "Members";
	/**
	 * docId
	 */
	private String id;
	/**
	 * 数据分两大类，一个是基础数据 BaseData，一个业务实时数据 UserData
	 */
	private String dataType = "BaseData";
	/**
	 * 会员名称
	 */
	private String name;
	/**
	 *电话号码
	 */
 	private String mobile;
	/**
	 *邮箱
	 */
	private String email;
	/**
	 *生日
	 */
	private String brithday;
	/**
	 *收件地址
	 */
	private String address;
	/**
	 *卡号
	 */
	private String cardNum;
	/**
	 *	 *该会员下赠送的总额，是在memberLogs变化 的时候更新的

	 */
	private float chargeTotal;

	/**
	 *该会员下赠送的总额，是在memberLogs变化 的时候更新的
	 */
	private float chargePresentTotal;


	/**
	 *卡状态 0、 正常 1、已挂失 2、已消卡
	 */
	private int state;
	/**
	 *创建日期
	 */
	private String createdTime;
	/**
	 *操作者
	 */
	private Employee creator;
	/**
	 *拥有的营销类型
	 */
	private String promotionId;
	/**
	 * 会员注销时为false
	 */
	private boolean valid;


	public Members() {
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getBrithday() {
		return brithday;
	}

	public void setBrithday(String brithday) {
		this.brithday = brithday;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCardNum() {
		return cardNum;
	}

	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}

	public float getChargeTotal() {
		return chargeTotal;
	}

	public void setChargeTotal(float chargeTotal) {
		this.chargeTotal = chargeTotal;
	}

	public float getChargePresentTotal() {
		return chargePresentTotal;
	}

	public void setChargePresentTotal(float chargePresentTotal) {
		this.chargePresentTotal = chargePresentTotal;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public Employee getCreator() {
		return creator;
	}

	public void setCreator(Employee creator) {
		this.creator = creator;
	}

	public String getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(String promotionId) {
		this.promotionId = promotionId;
	}
}
