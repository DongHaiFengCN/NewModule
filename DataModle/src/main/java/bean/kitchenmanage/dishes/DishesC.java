package bean.kitchenmanage.dishes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: Dishes
 * @Description: 菜品类文件
 * @author loongsun
 * @date 2014-7-29 上午1:06:02
 *
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

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public DishesC()
	{
	}
	public DishesC(String channelId)
	{
		this.channelId=channelId;
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

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getDishesName() {
		return dishesName;
	}

	public void setDishesName(String dishesName) {
		this.dishesName = dishesName;
	}

	public String getDishesNameCode9() {
		return dishesNameCode9;
	}

	public void setDishesNameCode9(String dishesNameCode9) {
		this.dishesNameCode9 = dishesNameCode9;
	}

	public boolean isTakeout() {
		return takeout;
	}

	public void setTakeout(boolean takeout) {
		this.takeout = takeout;
	}

	public boolean isSpecialty() {
		return specialty;
	}

	public void setSpecialty(boolean specialty) {
		this.specialty = specialty;
	}

	public List<String> getTasteList() {
		return tasteList;
	}

	public void setTasteList(List<String> tasteList) {
		this.tasteList = tasteList;
	}

	public void addTastId(String id)
	{
		if(this.tasteList==null)
			this.tasteList=new ArrayList<>();
		this.tasteList.add(id);
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPicpath() {
		return picpath;
	}

	public void setPicpath(String picpath) {
		this.picpath = picpath;
	}

	public boolean isSetMenu() {
		return setMenu;
	}

	public void setSetMenu(boolean setMenu) {
		this.setMenu = setMenu;
	}

	public List<String> getDishesIdList() {
		return dishesIdList;
	}

	public void setDishesIdList(List<String> dishesIdList) {
		this.dishesIdList = dishesIdList;
	}
	public void addDishesId(String id)
	{
		if(this.dishesIdList==null)
			this.dishesIdList=new ArrayList<>();
		this.dishesIdList.add(id);
	}

	public String getDishesKindId() {
		return dishesKindId;
	}

	public void setDishesKindId(String dishesKindId) {
		this.dishesKindId = dishesKindId;
	}




	public boolean isHaveSupDishes() {
		return haveSupDishes;
	}

	public void setHaveSupDishes(boolean haveSupDishes) {
		this.haveSupDishes = haveSupDishes;
	}

	public String getSupDishesId() {
		return supDishesId;
	}

	public void setSupDishesId(String supDishesId) {
		this.supDishesId = supDishesId;
	}

	public float getSupCount() {
		return supCount;
	}

	public void setSupCount(float supCount) {
		this.supCount = supCount;
	}

	public float getSupPrice() {
		return supPrice;
	}

	public void setSupPrice(float supPrice) {
		this.supPrice = supPrice;
	}

	public String getSupDishesName() {
		return supDishesName;
	}

	public void setSupDishesName(String supDishesName) {
		this.supDishesName = supDishesName;
	}

	public String getDishesNameCode26() {
		return dishesNameCode26;
	}

	public void setDishesNameCode26(String dishesNameCode26) {
		this.dishesNameCode26 = dishesNameCode26;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}


}
