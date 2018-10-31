package bean.kitchenmanage.order;


/**
 * @ClassName: Goods
 * @Description: 商品类文件
 * @author loongsun
 * @date 2017-01-06
 *
 */
public class Goods implements Cloneable {

	/**
	 * 方便对比
	 */

	private String id;
	/**
	 * 所包含菜品对象
	 */

	private String dishesId;
	/**
	 * 所选菜品口味
	 */
	private String dishesTaste;
	/**
	 * 所选菜品个数
	 */
	private float dishesCount;
	/**
	 * 菜品价格
	 */
	private float price;
	/**
	 *会员价格,点餐时要显示会员价格
	 */
	private float memberPrice;
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
    private int waitCall;
	/**
	 *退菜标识 0.正常  1.退菜 2.赠菜 3.自定义菜
	 */
	private int goodsType;
	/**
	 * 创建时间、
	 */
	private String createdTime;

	/**
	 * 菜品是否生成订单、 1、生成订单   2、未生成订单
	 */
	private int state;
	/**
	 * 订单备注
	 */
	private String description;
	/**
	 *打印标志flag为0, 不需要打印；1,需要打印，但未打印，2，需要打印，已打印
	 * V2.0
	 */
	private int printFlag;

	public Goods() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public float getDishesCount() {
		return dishesCount;
	}

	public void setDishesCount(float dishesCount) {
		this.dishesCount = dishesCount;
	}

	public int getWaitCall() {
		return waitCall;
	}

	public void setWaitCall(int waitCall) {
		this.waitCall = waitCall;
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

	public float getMemberPrice() {
		return memberPrice;
	}

	public void setMemberPrice(float memberPrice) {
		this.memberPrice = memberPrice;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPrintFlag() {
		return printFlag;
	}

	public void setPrintFlag(int printFlag) {
		this.printFlag = printFlag;
	}
}
