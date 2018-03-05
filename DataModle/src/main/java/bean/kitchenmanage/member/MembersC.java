package bean.kitchenmanage.member;
/**
 * @ClassName: Members
 * @Description: 会员类文件
 * @author loongsun
 * @date 2017-01-06
 *
 */


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bean.kitchenmanage.user.UsersC;

public class MembersC {
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
	 * 会员名称
	 */
	private String name;//会员名称
	/**
	 *
	 */
 	private String tel;//电话号码
	/**
	 *
	 */
	private String email;//邮箱
	/**
	 *
	 */
	private Date brithday;//生日
	/**
	 *地址
	 */
	private String address;
	/**
	 *
	 */
	private String cardNum;//卡号
	/**
	 *
	 */
	private float remainder;//余额
	/**
	 *卡状态 1、 正常 2、已挂失 3、已消卡
	 */
	private int status;
	/**
	 *
	 */
	private Date createdTime;//创建日期
	/**
	 *操作者
	 */
	private UsersC creator;
//	/**
//	 *卡类型名称
//	 */
//	private String  cardTypeName;
	/**
	 	 *卡类型名称
	  */
 	private String  cardTypeId;
	/**
	 *充值记录
	 */
	private List<String> rechargeLogIdList;
	/**
	 *消费记录
	 */
	private List<String> consumLogIdList;
	/**
	 *注销记录
	 */
	private String  cancelCardLogId;

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


	public MembersC() {
	}

	public MembersC(String company_id) {
		this.channelId = company_id;
		this.className="MembersC";
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



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getBrithday() {
		return brithday;
	}

	public void setBrithday(Date brithday) {
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

	public float getRemainder() {
		return remainder;
	}

	public void setRemainder(float remainder) {
		this.remainder = remainder;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public UsersC getCreator() {
		return creator;
	}

	public void setCreator(UsersC creator) {
		this.creator = creator;
	}



	public String getCardTypeId() {
		return cardTypeId;
	}

	public void setCardTypeId(String cardTypeId) {
		this.cardTypeId = cardTypeId;
	}

	public List<String> getConsumLogIdList() {
		return consumLogIdList;
	}

	public void setConsumLogIdList(List<String> consumLogIdList) {
		this.consumLogIdList = consumLogIdList;
	}

	public List<String> getRechargeLogIdList() {
		return rechargeLogIdList;
	}

	public void setRechargeLogIdList(List<String> rechargeLogIdList) {
		this.rechargeLogIdList = rechargeLogIdList;
	}

	public String getCancelCardLogId() {
		return cancelCardLogId;
	}

	public void setCancelCardLogId(String cancelCardLogId) {
		this.cancelCardLogId = cancelCardLogId;
	}

	public void addRechargeLog(String id)
	{
		if(this.rechargeLogIdList==null)
			rechargeLogIdList=new ArrayList<String>();
		rechargeLogIdList.add(id);
	}
	public void addConsumLog(String id)
	{
		if(this.consumLogIdList==null)
			consumLogIdList=new ArrayList<String>();
		consumLogIdList.add(id);
	}

}
