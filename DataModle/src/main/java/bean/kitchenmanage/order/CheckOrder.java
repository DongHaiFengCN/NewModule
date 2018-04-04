/**   
* @Title: CheckOrder.java 
* @Package com.canyin.db4o.zhifu 
* @Description: TODO(用一句话描述该文件做什么) 
* @author loongsun 
* @date 2014-7-29 上午2:11:34 
* @version V1.0   
*/
package bean.kitchenmanage.order;


import java.util.ArrayList;
import java.util.List;

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
	private List<String> orderIds;
	/**
	 * 结账日期
	 *  yyyy-MM-dd HH:mm:ss
	 *
	 */
	private String checkTime;
	/**
	 * 所属桌位号
	 */
    private String tableNum;
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
	private String promotionId;
	/**
	 * 活动优惠值
	 */
	private float  promotionDiscount;
	/**
	 * 会员手机号码；会员优惠怎么查询，需注意
	 */
	private String memberMobile;
	/**
	 * 支付细节
	 */
	private List<PayDetailC> payDetailList;
	/**
	 * 挂单信息引用；注意挂单统计时怎么使用这个字段。
	 */
	private HangInfo   hangInfo;
	/**
	 * 操作员名称
	 */
	private String employeeName;

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

	public List<String> getOrderIds() {
		return orderIds;
	}

	public void setOrderIds(List<String> orderIds) {
		this.orderIds = orderIds;
	}

	public String getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(String checkTime) {
		this.checkTime = checkTime;
	}

	public String getTableNum() {
		return tableNum;
	}

	public void setTableNum(String tableNum) {
		this.tableNum = tableNum;
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

	public String getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(String promotionId) {
		this.promotionId = promotionId;
	}

	public float getPromotionDiscount() {
		return promotionDiscount;
	}

	public void setPromotionDiscount(float promotionDiscount) {
		this.promotionDiscount = promotionDiscount;
	}

	public String getMemberMobile() {
		return memberMobile;
	}

	public void setMemberMobile(String memberMobile) {
		this.memberMobile = memberMobile;
	}

	public List<PayDetailC> getPayDetailList() {
		return payDetailList;
	}

	public void setPayDetailList(List<PayDetailC> payDetailList) {
		this.payDetailList = payDetailList;
	}

	public HangInfo getHangInfo() {
		return hangInfo;
	}

	public void setHangInfo(HangInfo hangInfo) {
		this.hangInfo = hangInfo;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
}
