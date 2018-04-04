package bean.kitchenmanage.MenuSystems;

import java.util.HashMap;

/**
 * 菜单体系
 * <p>
 * Created by loongsun on 2018/4/3.
 * <p>
 * email: 125736964@qq.com
 */

public class MenuSystem {
    /**
     * 公司唯一身份id,用于数据同步,做为唯一管道符
     */
    private String channelId;
    /**
     * 类名，用于数据库查询类过滤
     */
    private String className = "MenuSystem";
    /**
     * docId
     */
    private String id;
    /**
     * 数据分两大类，一个是基础数据 BaseData，一个业务实时数据 UserData
     */
    private String dataType = "BaseData";
    /**
     * 菜单体系名称
     */
    private String name;
    /**
     * 菜单体系使用开始时间
     */
    private String startTime;
    /**
     * 菜单体系使用结束时间，时间到期时要进行提示用户，由用户决定是否仍然使用，然后设置
     * using为false
     */
    private String endTime;
    /**
     * 是否有效使用,false 无效；true有效
     */
    private boolean using;
    /**
     *对应菜品基础价格，自定义
     */
    private HashMap<String, Float> basePrices;
    /**
     *对应菜品会员价格，自定义
     */
    private HashMap<String,Float> memberPrices;

}
