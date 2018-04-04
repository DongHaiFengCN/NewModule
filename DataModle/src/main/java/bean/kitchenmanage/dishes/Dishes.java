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
	private Blob image;
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


}
