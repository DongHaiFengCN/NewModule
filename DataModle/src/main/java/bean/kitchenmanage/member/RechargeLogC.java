package bean.kitchenmanage.member;


import java.util.Date;

import bean.kitchenmanage.user.UsersC;

/**
 * @ClassName: RechargeLog
 * @Description: 充值记录
 * @author loongsun
 * @date 2017-01-06
 *
 */

public class RechargeLogC {
	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className;
	/**
	 *
	 * docId
	 */
	private String _id;

	/**
	 * 会员id
	 */
	private String  membersId;
	/**
	 *充值金额
	 */
    private float recharge;   		  //充值金额
	/**
	 * 充值日期
	 */
	private  Date createdate;     //创建日期
	/**
	 *操作人
	 */
	private UsersC operator;
	/**
	 *卡类型
	 */
    private CardTypeC cardTypeC;

	/**
	 * 支付方式
	 */
	private String payType;

	/**
	 * 只有业务数据才有年份划分:2018
	 */
	private String createdYear;
	/**
	 * 数据分两大类，一个是基础数据 basic，一个业务实时数据 business
	 */
	private String dataType = "UserData";

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



	public RechargeLogC() {
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public RechargeLogC(String company_id) {
		this.channelId = company_id;
		this.className="RechargeLogC";
	}



	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
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

	public String getMembersId() {
		return membersId;
	}

	public void setMembersId(String membersId) {
		this.membersId = membersId;
	}

	public float getRecharge() {
		return recharge;
	}

	public void setRecharge(float recharge) {
		this.recharge = recharge;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	public UsersC getOperator() {
		return operator;
	}

	public void setOperator(UsersC operator) {
		this.operator = operator;
	}

	public CardTypeC getCardTypeC() {
		return cardTypeC;
	}

	public void setCardTypeC(CardTypeC cardTypeC) {
		this.cardTypeC = cardTypeC;
	}
}
