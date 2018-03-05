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

import bean.kitchenmanage.user.UsersC;

/**
 * @ClassName: CheckOrder 
 * @Description 买单类
 * @author loongsun
 * @date 2014-7-29 上午2:11:34 
 *  
 */
public class CheckOrderC {
	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className;
	/**
	 * docId
	 */
	private String _id;

	/**
	 * 包含的订单
	 */
	private List<OrderC> orderList;
	/**
	 * 结账日期
	 *  yyyy-MM-dd HH:mm:ss
	 *  之所以定义成String型，是因为 DB4o 按该字段排序时不支持 Date型
	 */
	private String checkTime;
	/**
	 * 所属桌位号
	 */
    private String tableNo;
	/**
	 *实收
	 */
	private float pay;
	/**
	 *应收
	 */
	private float needPay;

	/**
	 * Class description ：营销方式下的支付详情类
	 */
	private PromotionDetailC promotionDetail;
	/**
	 * 操作员
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


	public CheckOrderC()
	{

	}
	public CheckOrderC(String company_id)
	{
		this.channelId = company_id;
		this.className ="CheckOrderC";
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

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public List<OrderC> getOrderList() {
		return orderList;
	}
	public void addOrder(OrderC obj)
	{
		if(orderList==null)
			orderList=new ArrayList<OrderC>();
		orderList.add(obj);
	}
	public void setOrderList(List<OrderC> orderList) {
		this.orderList = orderList;
	}

	public String getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(String checkTime) {
		this.checkTime = checkTime;
	}

	public String getTableNo() {
		return tableNo;
	}

	public void setTableNo(String tableNo) {
		this.tableNo = tableNo;
	}

	public float getPay() {
		return pay;
	}

	public void setPay(float pay) {
		this.pay = pay;
	}

	public float getNeedPay() {
		return needPay;
	}

	public void setNeedPay(float needPay) {
		this.needPay = needPay;
	}

	public PromotionDetailC getPromotionDetail() {
		return promotionDetail;
	}

	public void setPromotionDetail(PromotionDetailC promotionDetail) {
		this.promotionDetail = promotionDetail;
	}

	public UsersC getOperator() {
		return operator;
	}
	public void setOperator(UsersC operator)
	{
		this.operator = operator;
	}
}
