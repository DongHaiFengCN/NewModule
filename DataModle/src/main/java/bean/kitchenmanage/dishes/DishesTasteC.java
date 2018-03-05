package bean.kitchenmanage.dishes;

import java.io.Serializable;

/**
 * @ClassName: DishesTaste
 * @Description: 口味类文件
 * @author loongsun
 * @date 2014-7-29 上午1:06:02
 *
 */
public class DishesTasteC implements Serializable {
	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className;
	/**
	 * 口味名称，口味不要重名，添加时要做判断
	 */
	/**
	 * 对象id,等于docmentid,一般用于Pojo操作时使用。
	 */
	private String _id;

	private String tasteName;
	/**
	 * 是否有效
	 */
	private boolean valid;
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

	public DishesTasteC()
	{
	}
	public DishesTasteC(String channelId, String tasteName)
	{
		this.channelId = channelId;
		this.tasteName = tasteName;
		this.className="DishesTasteC";
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

	public String getTasteName() {
		return tasteName;
	}

	public void setTasteName(String tasteName) {
		this.tasteName = tasteName;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
}
