package bean.kitchenmanage.table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: Area
 * @Description: 区域类文件
 * @author
 * @date 2017-05-15
 *
 */
public class Area  {
	/**
	 * 对象id,等于docmentid,一般用于Pojo操作时使用。
	 */
	private String id;
	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className = "Area";
	/**
	 * 数据分两大类，一个是基础数据 BaseData，一个业务实时数据 UserData
	 */
	private String dataType = "BaseData";
	/**
	 * 区域名称，名称唯一，增加时判断唯一
	 */
	private String name;
	/**
	 * 区域号，001开始，唯一性
	 */
	private String num;
	/**
	 * 是否有效
	 */
	private boolean valid;


	public Area() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
}
