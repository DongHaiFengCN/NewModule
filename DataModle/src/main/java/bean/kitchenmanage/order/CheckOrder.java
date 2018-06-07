/**   
* @Title: CheckOrder.java 
* @Package com.canyin.db4o.zhifu 
* @Description: TODO(用一句话描述该文件做什么) 
* @author loongsun 
* @date 2014-7-29 上午2:11:34 
* @version V1.0   
*/
package bean.kitchenmanage.order;


import java.util.List;

import bean.kitchenmanage.member.Members;
import bean.kitchenmanage.promotion.Promotion;

/**
 * @ClassName: CheckOrder 
 * @Description 买单类
 * @author loongsun
 * @date 2014-7-29 上午2:11:34 
 *  
 */
public class CheckOrder {
	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className = "checkOrder";
	/**
	 * docId
	 */
	private String id;
	/**
	 * 数据分两大类，一个是基础数据 BaseData，一个业务实时数据 UserData
	 */
	private String dataType = "UserData";

	/**
	 * 只有业务数据才有年份划分:2018
	 */
	private String createdYear;
	/**
	 * 包含的订单
	 */
	private List<String> orderId;
	/**
	 * 结账日期
	 *  yyyy-MM-dd HH:mm:ss
	 *
	 */
	private String checkTime;
	/**
	 * 所属桌位Id
	 */
    private String tableId;
	/**
	 *实收
	 */
	private float lastPay;
	/**
	 *应收
	 */
	private float needPay;
	/**
	 * 活动docId;活动优惠怎么处理，需要注意
	 */
	private Promotion promotion;
	/**
	 * 操作员名称
	 */
	private Members member;
	/**
	 * 会员优惠或活动优惠详情
	 */
	private List<PromotionDiscountDetail> promotionDiscountDetailList;
	/**
	 * 支付细节
	 */
	private List<PayDetail> payDetailList;
	/**
	 * 挂单信息引用；注意挂单统计时怎么使用这个字段。
	 */
	private HangInfo  hangInfo;
	/**
	 * 操作员名称
	 */
	private String operator;


	public CheckOrder() {
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

	public String getCreatedYear() {
		return createdYear;
	}

	public void setCreatedYear(String createdYear) {
		this.createdYear = createdYear;
	}

	public List<String> getOrderId() {
		return orderId;
	}

	public void setOrderId(List<String> orderId) {
		this.orderId = orderId;
	}

	public String getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(String checkTime) {
		this.checkTime = checkTime;
	}


	public float getLastPay() {
		return lastPay;
	}

	public void setLastPay(float lastPay) {
		this.lastPay = lastPay;
	}

	public float getNeedPay() {
		return needPay;
	}

	public void setNeedPay(float needPay) {
		this.needPay = needPay;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public Promotion getPromotion() {
		return promotion;
	}

	public void setPromotion(Promotion promotion) {
		this.promotion = promotion;
	}

	public List<PayDetail> getPayDetailList() {
		return payDetailList;
	}

	public void setPayDetailList(List<PayDetail> payDetailList) {
		this.payDetailList = payDetailList;
	}

	public HangInfo getHangInfo() {
		return hangInfo;
	}

	public void setHangInfo(HangInfo hangInfo) {
		this.hangInfo = hangInfo;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Members getMember() {
		return member;
	}

	public void setMember(Members member) {
		this.member = member;
	}

	public List<PromotionDiscountDetail> getPromotionDiscountDetailList() {
		return promotionDiscountDetailList;
	}

	public void setPromotionDiscountDetailList(List<PromotionDiscountDetail> promotionDiscountDetailList) {
		this.promotionDiscountDetailList = promotionDiscountDetailList;
	}
}
