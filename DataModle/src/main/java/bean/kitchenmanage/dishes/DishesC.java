package bean.kitchenmanage.dishes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Dishes c.
 *
 * @author loongsun
 * @ClassName: Dishes
 * @Description: 菜品类文件
 * @date 2014 -7-29 上午1:06:02
 */
public class DishesC implements Serializable {
	/**
	 *对象id,等于docmentid,一般用于Pojo操作时使用。
	 */
	private  String _id;
	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className = "DishesC";
	/**
	 * 菜品的序号，代表的添加顺序，可以用它来做显示顺序
	 */
	private int orderId;
	/**
	 * 菜品名称
	 */
	private String dishesName;
	/**
	 * 菜品名称的code9码,用于终端9数字键盘点餐
	 */
	private String dishesNameCode9;
	/**
	 *菜品名称26个首字母
	 */
	private String dishesNameCode26;
	/**
	 * 是否外卖 true 是外卖菜品，false 非外卖菜品
	 */
	private boolean takeout;//是否外卖，未使用
	/**
	 * 是否招牌菜
	 */
	private boolean specialty;//是否招牌菜 ，未使用
	/**
	 *所含菜品口味Id列表，
	 */
	private List<String> tasteList;
	/**
	 *菜品价格
	 */
	private float price;
	/**
	 *菜品描述
	 */
	private String description;
	/**
	 *菜品图片路径,保存大图片在了本地
	 */
	private String picpath;
	/**
	 * 入库图片
	 */
	//private String  image;//blob内容
	/**
	 *所属菜品类名称
	 */
	private String dishesKindId;
	/**
	 *是否是二级套餐,在设置二级套餐时，以菜品类方式代替，便于与通常菜品一起展示操作
	 * true:为二级套餐。false为正常菜品
	 */
	private boolean setMenu;
	/**
	 *二级套餐下的所包含菜品
	 */
	private List<String> dishesIdList;

	/**
	 * 是否有子商品
	 */
	private boolean haveSupDishes;
	/**
	 * 辅助计量单位id
	 */
	private String supDishesId;
	/**
	 * 主计量1个单位包含几个辅助计量单位
	 */
	private float supCount;
	/**
	 * 辅助计量单位价格
	 */
	private float  supPrice;
	/**
	 * 辅助计量单位名字
	 */
	private String supDishesName;
	/**
	 * 数据分两大类，一个是基础数据 basic，一个业务实时数据 business
	 */
	private String dataType = "BaseData";//basi,代表basic数据,busi,代表业务数据
	/**
	 * 菜品状态，0为正常，1为估清
	 */
	private int state;

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
	 * Instantiates a new Dishes c.
	 */
	public DishesC()
	{
	}

	/**
	 * Instantiates a new Dishes c.
	 *
	 * @param channelId the channel id
	 */
	public DishesC(String channelId)
	{
		this.channelId=channelId;
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
	 * Gets order id.
	 *
	 * @return the order id
	 */
	public int getOrderId() {
		return orderId;
	}

	/**
	 * Sets order id.
	 *
	 * @param orderId the order id
	 */
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	/**
	 * Gets dishes name.
	 *
	 * @return the dishes name
	 */
	public String getDishesName() {
		return dishesName;
	}

	/**
	 * Sets dishes name.
	 *
	 * @param dishesName the dishes name
	 */
	public void setDishesName(String dishesName) {
		this.dishesName = dishesName;
	}

	/**
	 * Gets dishes name code 9.
	 *
	 * @return the dishes name code 9
	 */
	public String getDishesNameCode9() {
		return dishesNameCode9;
	}

	/**
	 * Sets dishes name code 9.
	 *
	 * @param dishesNameCode9 the dishes name code 9
	 */
	public void setDishesNameCode9(String dishesNameCode9) {
		this.dishesNameCode9 = dishesNameCode9;
	}

	/**
	 * Is takeout boolean.
	 *
	 * @return the boolean
	 */
	public boolean isTakeout() {
		return takeout;
	}

	/**
	 * Sets takeout.
	 *
	 * @param takeout the takeout
	 */
	public void setTakeout(boolean takeout) {
		this.takeout = takeout;
	}

	/**
	 * Is specialty boolean.
	 *
	 * @return the boolean
	 */
	public boolean isSpecialty() {
		return specialty;
	}

	/**
	 * Sets specialty.
	 *
	 * @param specialty the specialty
	 */
	public void setSpecialty(boolean specialty) {
		this.specialty = specialty;
	}

	/**
	 * Gets taste list.
	 *
	 * @return the taste list
	 */
	public List<String> getTasteList() {
		return tasteList;
	}

	/**
	 * Sets taste list.
	 *
	 * @param tasteList the taste list
	 */
	public void setTasteList(List<String> tasteList) {
		this.tasteList = tasteList;
	}

	/**
	 * Add tast id.
	 *
	 * @param id the id
	 */
	public void addTastId(String id)
	{
		if(this.tasteList==null)
			this.tasteList=new ArrayList<>();
		this.tasteList.add(id);
	}

	/**
	 * Gets price.
	 *
	 * @return the price
	 */
	public float getPrice() {
		return price;
	}

	/**
	 * Sets price.
	 *
	 * @param price the price
	 */
	public void setPrice(float price) {
		this.price = price;
	}

	/**
	 * Gets description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets description.
	 *
	 * @param description the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets picpath.
	 *
	 * @return the picpath
	 */
	public String getPicpath() {
		return picpath;
	}

	/**
	 * Sets picpath.
	 *
	 * @param picpath the picpath
	 */
	public void setPicpath(String picpath) {
		this.picpath = picpath;
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
	 * Gets dishes id list.
	 *
	 * @return the dishes id list
	 */
	public List<String> getDishesIdList() {
		return dishesIdList;
	}

	/**
	 * Sets dishes id list.
	 *
	 * @param dishesIdList the dishes id list
	 */
	public void setDishesIdList(List<String> dishesIdList) {
		this.dishesIdList = dishesIdList;
	}

	/**
	 * Add dishes id.
	 *
	 * @param id the id
	 */
	public void addDishesId(String id)
	{
		if(this.dishesIdList==null)
			this.dishesIdList=new ArrayList<>();
		this.dishesIdList.add(id);
	}

	/**
	 * Gets dishes kind id.
	 *
	 * @return the dishes kind id
	 */
	public String getDishesKindId() {
		return dishesKindId;
	}

	/**
	 * Sets dishes kind id.
	 *
	 * @param dishesKindId the dishes kind id
	 */
	public void setDishesKindId(String dishesKindId) {
		this.dishesKindId = dishesKindId;
	}

	/**
	 * Gets state.
	 *
	 * @return the state
	 */
	public int getState() {
		return state;
	}

	/**
	 * Sets state.
	 *
	 * @param state the state
	 */
	public void setState(int state) {
		this.state = state;
	}

	/**
	 * Is have sup dishes boolean.
	 *
	 * @return the boolean
	 */
	public boolean isHaveSupDishes() {
		return haveSupDishes;
	}

	/**
	 * Sets have sup dishes.
	 *
	 * @param haveSupDishes the have sup dishes
	 */
	public void setHaveSupDishes(boolean haveSupDishes) {
		this.haveSupDishes = haveSupDishes;
	}

	/**
	 * Gets sup dishes id.
	 *
	 * @return the sup dishes id
	 */
	public String getSupDishesId() {
		return supDishesId;
	}

	/**
	 * Sets sup dishes id.
	 *
	 * @param supDishesId the sup dishes id
	 */
	public void setSupDishesId(String supDishesId) {
		this.supDishesId = supDishesId;
	}

	/**
	 * Gets sup count.
	 *
	 * @return the sup count
	 */
	public float getSupCount() {
		return supCount;
	}

	/**
	 * Sets sup count.
	 *
	 * @param supCount the sup count
	 */
	public void setSupCount(float supCount) {
		this.supCount = supCount;
	}

	/**
	 * Gets sup price.
	 *
	 * @return the sup price
	 */
	public float getSupPrice() {
		return supPrice;
	}

	/**
	 * Sets sup price.
	 *
	 * @param supPrice the sup price
	 */
	public void setSupPrice(float supPrice) {
		this.supPrice = supPrice;
	}

	/**
	 * Gets sup dishes name.
	 *
	 * @return the sup dishes name
	 */
	public String getSupDishesName() {
		return supDishesName;
	}

	/**
	 * Sets sup dishes name.
	 *
	 * @param supDishesName the sup dishes name
	 */
	public void setSupDishesName(String supDishesName) {
		this.supDishesName = supDishesName;
	}

	/**
	 * Gets dishes name code 26.
	 *
	 * @return the dishes name code 26
	 */
	public String getDishesNameCode26() {
		return dishesNameCode26;
	}

	/**
	 * Sets dishes name code 26.
	 *
	 * @param dishesNameCode26 the dishes name code 26
	 */
	public void setDishesNameCode26(String dishesNameCode26) {
		this.dishesNameCode26 = dishesNameCode26;
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


}
