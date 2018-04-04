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
public class DishesKind implements Serializable
{
	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className="DishesKind";
	/**
	 * 对象id,等于docmentid,一般用于Pojo操作时使用。
	 */
	private String id;
	/**
	 * 数据分两大类，一个是基础数据 BaseData，一个业务实时数据 UserData
	 */
	private String dataType = "BaseData";
	/**
	 * 菜品类别名称
	 */
	private String name;
	/**
	 * 添加菜品时间
	 */
	private String  createTime;

	/**
	 * Instantiates a new Dishes kind c.
	 */
	public DishesKind()
	{
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}
