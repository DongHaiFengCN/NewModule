package bean.kitchenmanage.user;

import java.util.ArrayList;
import java.util.List;


/**
 * 项目名称：T
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2018/4/18 9:44
 * 修改人：donghaifeng
 * 修改时间：2018/4/18 9:44
 * 修改备注：*/



public class Role {

	/**
	 * 数据分两大类，一个是基础数据 BaseData，一个业务实时数据 UserData
	 */
	private String dataType = "BaseData";
	/**
	 *角色描述
	 */
	private String description;

	public String getDataType() {
		return dataType == null ? "" : dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}


	public void setChannleId(String channleId) {
		this.channleId = channleId;
	}

	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channleId;

	public String getId() {
		return id == null ? "" : id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private String id;
	/**
	 *角色名称
	 */
	private String name;

	private List<Menu> menuList;

	public String getClassName() {
		return className == null ? "" : className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	private String className;


	public void setMenuList(List<Menu> menuList) {
		this.menuList = menuList;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public void addMenu(Menu menu) {

		if(menuList == null){

			menuList = new ArrayList<>();
		}
		menuList.add(menu);
	}
	public List<Menu> getMenuList() {
		return menuList;
	}
}
