package bean.kitchenmanage.order;


/**
 * @ClassName: Goods
 * @Description: 商品类文件
 * @author loongsun
 * @date 2017-01-06
 *
 */
public class GoodsC implements Cloneable {
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
	 * 所属订单对象
	 */
	private String order;//订单号
	/**
	 * 所包含菜品对象
	 */
//	private DishesC dishes;
	private String dishesId;
	/**
	 * 所选菜品口味
	 */
	private String dishesTaste;
	/**
	 * 所选菜品个数
	 */
	private float DishesCount;
	/**
	 * 所选菜品个数
	 */
	private float price;
	/**
	 * 所选菜品名称
	 */
	private String dishesName;
	/**
	 * 所选菜类Id
	 */
	private String dishesKindId;
	/**
	 * 是否是待叫商品,0，代表正常上菜；1代表待叫菜品，打印时加备注
	 */
    private int isWaitCall;

	/**
	 *退菜标识 0.正常  1.退菜 2.赠菜 3.自定义菜
	 */
	private int goodsType;
	/**
	 *用于产品排序及热度排行
	 */
	private String createdTime;// 日期时间****

	/**
	 * 商品备注
	 */
    private String desc;
	public GoodsC() {
	}

	public GoodsC(String company_id) {
		this.channelId = company_id;
		this.className="GoodsC";
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

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}


	public String getDishesKindId() {
		return dishesKindId;
	}

	public void setDishesKindId(String dishesKindId) {
		this.dishesKindId = dishesKindId;
	}

	public String getDishesId() {
		return dishesId;
	}

	public void setDishesId(String dishesId) {
		this.dishesId = dishesId;
	}

	public String getDishesTaste() {
		return dishesTaste;
	}

	public void setDishesTaste(String dishesTaste) {
		this.dishesTaste = dishesTaste;
	}

	public float getDishesCount() {
		return DishesCount;
	}

	public void setDishesCount(float dishesCount) {
		DishesCount = dishesCount;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getDishesName() {
		return dishesName;
	}

	public void setDishesName(String dishesName) {
		this.dishesName = dishesName;
	}

	public int getIsWaitCall() {
		return isWaitCall;
	}

	public void setIsWaitCall(int isWaitCall) {
		this.isWaitCall = isWaitCall;
	}

	public int getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(int goodsType) {
		this.goodsType = goodsType;
	}
	public Object clone() throws CloneNotSupportedException {
		return super.clone(); // 具体的克隆操作由父类完成
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
