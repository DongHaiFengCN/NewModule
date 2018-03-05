package bean.kitchenmanage.user;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * 
 * @author sdses1
 * 
 */
public class RoleModelC {
	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className;
	/**
	 * 角色id
	 */
	private String roleID;
	/**
	 *父角色id
	 */
	private String rolePID;
	/**
	 *角色名称
	 */
	private String roleName;
	/**
	 *角色描述
	 */
	private String roleDesc;
	/**
	 *所包含的菜单列表
	 */
	private List<MenuModelC> menuList = new ArrayList<MenuModelC>();

}
