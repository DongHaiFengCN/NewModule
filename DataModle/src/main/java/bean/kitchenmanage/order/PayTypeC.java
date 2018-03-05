/**   
* @Title: CheckType.java 
* @Package com.canyin.db4o.zhifu 
* @Description: TODO(用一句话描述该文件做什么) 
* @author loongsun 
* @date 2014-7-29 上午2:13:43 
* @version V1.0   
*/
package bean.kitchenmanage.order;


/** 
 * @ClassName: PayType
 * @Description: 支付类型
 * @author loongsun
 * @date 2014-7-29 上午2:13:43 
 *  
 */
public class PayTypeC {
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
	 * 支付类型名称
	 */
	private int type;//支付类型
	/**
	 * 是否有效
	 */
	private int isValid;//是否有效

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



	public PayTypeC()
	{
	}

	public PayTypeC(String company_id, int type, int isValid) {
		this.channelId = company_id;
		this.type = type;
		this.isValid = isValid;
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getIsValid() {
		return isValid;
	}

	public void setIsValid(int isValid) {
		this.isValid = isValid;
	}
}
