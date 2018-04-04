package bean.kitchenmanage.dishes;

import java.io.Serializable;

/**
 * @ClassName: DishesTaste
 * @Description: 口味类文件
 * @author loongsun
 * @date 2014-7-29 上午1:06:02
 *
 */
public class Taste implements Serializable {
	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className = "Taste";
	/**
	 * 对象id,等于docmentid,一般用于Pojo操作时使用。
	 */
	private String id;
	/**
	 * 数据分两大类，一个是基础数据 BaseData，一个业务实时数据 UserData
	 */
	private String dataType = "BaseData";
	/**
	 * 口味名称
	 */
	private String name;
	/**
	 * 是否有效
	 */
	private boolean valid;


	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Taste()
	{
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
}
