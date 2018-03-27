package bean.kitchenmanage.dishes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The type Dishes kind c.
 *
 * @author loongsun
 * @ClassName:DishesKind
 * @Description: 菜品类文件
 * @date 2017 -7-29 上午1:06:02
 */
public class DishesKindC implements Serializable
{
	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className;
	/**
	 * 对象id,等于docmentid,一般用于Pojo操作时使用。
	 */
	private String _id;
	/**
	 * 菜品类别名称
	 */
	private String kindName;
	/**
	 * 是否是一级套餐
	 * * true:为一级套餐。false为正常菜类
	 */
	private boolean setMenu;//是否是一级套餐

	/**
	 *
	 * 该类下所包含的菜品
	 */
	private List<String> dishesIdList;//下属菜品

	/**
	 * 添加菜品时间
	 */
	private Date  createTime;
	/**
	 * 数据分两大类，一个是基础数据 basic，一个业务实时数据 business
	 */
	private String dataType = "BaseData";//basi,代表basic数据,busi,代表业务数据

	/**
	 * Gets data type.
	 *
	 * @return the data type
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * Sets data type.
	 *
	 * @param dataType the data type
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
	 * Instantiates a new Dishes kind c.
	 */
	public DishesKindC() {
	}

	/**
	 * Instantiates a new Dishes kind c.
	 *
	 * @param channelId the channel id
	 */
	public DishesKindC(String channelId) {
		this.channelId = channelId;
		this.className="DishesKindC";
	}

	/**
	 * Gets create time.
	 *
	 * @return the create time
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * Sets create time.
	 *
	 * @param createTime the create time
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * Gets channel id.
	 *
	 * @return the channel id
	 */
	public String getChannelId() {
		return channelId;
	}

	/**
	 * Sets channel id.
	 *
	 * @param channelId the channel id
	 */
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	/**
	 * Gets class name.
	 *
	 * @return the class name
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Sets class name.
	 *
	 * @param className the class name
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * Gets id.
	 *
	 * @return the id
	 */
	public String get_id() {
		return _id;
	}

	/**
	 * Sets id.
	 *
	 * @param _id the id
	 */
	public void set_id(String _id) {
		this._id = _id;
	}

	/**
	 * Gets kind name.
	 *
	 * @return the kind name
	 */
	public String getKindName() {
		return kindName;
	}

	/**
	 * Sets kind name.
	 *
	 * @param kindName the kind name
	 */
	public void setKindName(String kindName) {
		this.kindName = kindName;
	}

	/**
	 * Is set menu boolean.
	 *
	 * @return the boolean
	 */
	public boolean isSetMenu() {
		return setMenu;
	}

	/**
	 * Sets set menu.
	 *
	 * @param setMenu the set menu
	 */
	public void setSetMenu(boolean setMenu) {
		this.setMenu = setMenu;
	}


	/**
	 * Gets dishes list id.
	 *
	 * @return the dishes list id
	 */
	public List<String> getDishesListId() {
		if(dishesIdList==null)
			dishesIdList=new ArrayList<>();
		return dishesIdList;
	}

	/**
	 * Sets dishes list id.
	 *
	 * @param dishesIdList the dishes list id
	 */
	public void setDishesListId(List<String> dishesIdList) {
		this.dishesIdList = dishesIdList;
	}

	/**
	 * Add dishes id.
	 *
	 * @param id the id
	 */
	public void addDishesId(String id)
	{
		if(dishesIdList==null)
			dishesIdList=new ArrayList<>();
		dishesIdList.add(id);
	}



}
