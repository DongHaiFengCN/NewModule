package bean.kitchenmanage.user;
/**
 * @ClassName: MenuModel
 * @Description: 菜单类文件
 * @author loongsun
 * @date 2014-7-29 上午1:06:02
 *
 */

public class MenuModelC {

	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className;
	/**
	 * 菜单ID
	 */
	private String menuID;
	/**
	 * 菜单名称
	 */
	private String menuName;
	/**
	 * 菜单描述
	 */
	private String menuDesc;
	/**
	 * 父菜单ID
	 */
	private String menuPID;



}
