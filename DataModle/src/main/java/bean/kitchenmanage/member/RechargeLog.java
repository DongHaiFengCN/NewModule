package bean.kitchenmanage.member;


import java.util.Date;

import bean.kitchenmanage.user.Employee;

/**
 * @ClassName: RechargeLog
 * @Description: 充值记录
 * @author loongsun
 * @date 2017-01-06
 *
 */

public class RechargeLog {
	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className = "RechargeLog";
	/**
	 * 数据分两大类，一个是基础数据 BaseData，一个业务实时数据 UserData
	 */
	private String dataType = "UserData";
	/**
	 * 只有业务数据才有年份划分:2018
	 */
	private String createdYear;
	/**
	 * docId
	 */
	private String id;

	/**
	 * 会员Docid
	 */
	private String  memberId;
	/**
	 *充值金额
	 */
    private float recharge;
	/**
	 * 充值日期
	 */
	private  String createdTime;
	/**
	 *操作人
	 */
	private Employee operator;
	/**
	 *卡类型
	 */
    private CardType cardTypeC;
	/**
	 * 支付方式,
	 */
	private String payType;

	public RechargeLog() {
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

	public String getCreatedYear() {
		return createdYear;
	}

	public void setCreatedYear(String createdYear) {
		this.createdYear = createdYear;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public float getRecharge() {
		return recharge;
	}

	public void setRecharge(float recharge) {
		this.recharge = recharge;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public Employee getOperator() {
		return operator;
	}

	public void setOperator(Employee operator) {
		this.operator = operator;
	}

	public CardType getCardTypeC() {
		return cardTypeC;
	}

	public void setCardTypeC(CardType cardTypeC) {
		this.cardTypeC = cardTypeC;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}
}
