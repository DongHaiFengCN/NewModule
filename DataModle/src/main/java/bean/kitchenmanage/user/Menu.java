package bean.kitchenmanage.user;
/**
 * @ClassName: MenuModel
 * @Description: 菜单类文件
 * @author loongsun
 * @date 2014-7-29 上午1:06:02
 *
 */

public class Menu {

	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private  String id;
	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className = "Menu";
	/**
	 * 数据分两大类，一个是基础数据 BaseData，一个业务实时数据 UserData
	 */
	private String dataType = "BaseData";

	/**
	 * 菜单名称
	 */
	private String name;
	/**
	 * 菜单描述
	 */
	private String description;
	/**
	 * 父菜单ID
	 */
	private String parentId;

	public Menu() {
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
}
