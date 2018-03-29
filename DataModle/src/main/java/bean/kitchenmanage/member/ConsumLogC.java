package bean.kitchenmanage.member;


import java.util.Date;

import bean.kitchenmanage.user.UsersC;

/**
 * @ClassName: ConsumLog
 * @Description: 会员卡刷卡记录
 * @author loongsun
 * @date 2014-7-29 上午1:06:02
 *
 */

public class ConsumLogC {
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
	private String membersId;
	/**
	 * 卡号
	 */
	private String cardNum;
	/**
	 *订单号
	 */
	private String orderNum;
	/**
	 *消费金额
	 */
	private float cardConsum;       //卡消费金额
	/**
	 * 创建日期
	 */
	private Date time;
	/**
	 *录入人
	 */
	private UsersC operator;

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

	public ConsumLogC() {

	}

	public ConsumLogC(String company_id) {
		this.channelId = company_id;
		this.className="ConsumLogC";
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

	public String getCardNum() {
		return cardNum;
	}

	public void setCardNum(String cardNo) {
		this.cardNum = cardNo;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNo) {
		this.orderNum = orderNo;
	}

	public float getCardConsum() {
		return cardConsum;
	}

	public void setCardConsum(float cardConsum) {
		this.cardConsum = cardConsum;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public UsersC getOperator() {
		return operator;
	}

	public void setOperator(UsersC operator) {
		this.operator = operator;
	}
}
