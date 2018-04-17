package bean.kitchenmanage.dishes;

import java.io.Serializable;
import java.sql.Blob;
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
public class Dishes implements Serializable {
	/**
	 *对象id,等于docmentid,一般用于Pojo操作时使用。
	 */
	private  String id;
	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className ;
	/**
	 * 数据分两大类，一个是基础数据 BaseData，一个业务实时数据 UserData
	 */
	private String dataType;
	/**
	 * 菜品的序号，代表的添加顺序，可以用它来做显示顺序
	 */
	private int sortNum;
	/**
	 * 菜品名称 名称唯一
	 */
	private String name;
	/**
	 * 菜品名称的code9码,用于终端9数字键盘点餐
	 */
	private String code9;
	/**
	 *菜品名称26个首字母
	 */
	private String code26;
	/**
	 * 是否外卖 true 是外卖菜品，false 非外卖菜品
	 */
	private boolean takeout;
	/**
	 * 是否招牌菜
	 */
	private boolean specialty;
	/**
	 *所含菜品口味Id列表，
	 */
	private List<String> tasteIds;
	/**
	 * 菜品价格
	 */
	private float price;
	/**
	 *会员价格
	 */
	private float memberPrice;
	/**
	 *菜品描述
	 */
	private String description;
	/**
	 * 入库图片
	 */
	//private Blob image;
	/**
	 *所属菜品类名称
	 */
	private String kindId;
	/**
	 *套餐下的所包含菜品
	 */
	private List<String> dishesIds;
	/**
	 * 辅助计量单位id
	 */
	private String supDishesId;
	/**
	 * 主计量1个单位包含几个辅助计量单位
	 */
	private float supCount;

	/**
	 * 菜品状态，0为正常，1为估清
	 */
	private boolean sell;
	/**
	 * 创建时间
	 */
	private String createdTime;

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

	public int getSortNum() {
		return sortNum;
	}

	public void setSortNum(int sortNum) {
		this.sortNum = sortNum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode9() {
		return code9;
	}

	public void setCode9(String code9) {
		this.code9 = code9;
	}

	public String getCode26() {
		return code26;
	}

	public void setCode26(String code26) {
		this.code26 = code26;
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

	public List<String> getTasteIds() {
		return tasteIds;
	}

	public void setTasteIds(List<String> tasteIds) {
		this.tasteIds = tasteIds;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public float getMemberPrice() {
		return memberPrice;
	}

	public void setMemberPrice(float memberPrice) {
		this.memberPrice = memberPrice;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

//	public Blob getImage() {
//		return image;
//	}
//
//	public void setImage(Blob image) {
//		this.image = image;
//	}

	public String getKindId() {
		return kindId;
	}

	public void setKindId(String kindId) {
		this.kindId = kindId;
	}

	public List<String> getDishesIds() {
		return dishesIds;
	}

	public void setDishesIds(List<String> dishesIds) {
		this.dishesIds = dishesIds;
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

	public boolean isSell() {
		return sell;
	}

	public void setSell(boolean sell) {
		this.sell = sell;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
}
