package bean.kitchenmanage.depot;

import java.io.Serializable;

/**
 * @ClassName: DishesConsume
 * @Description: 菜品消费量
 * @author loongsun
 * @date 2016-01-01 上午1:19:08
 *
 */
public class DishConsum implements Serializable {
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
    private String className = "DishConsum";
    /**
     * 数据分两大类，一个是基础数据 BaseData，一个业务实时数据 UserData
     */
    private String dataType = "BaseData";
    /**
     * 菜品id
     */
    private String dishId;
    /**
     * 消耗数量
     */
    private float consums;


    public DishConsum()
    {
    }

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


    public float getConsums() {
        return consums;
    }

    public void setConsums(float consums) {
        this.consums = consums;
    }

    public String getDishId() {
        return dishId;
    }

    public void setDishId(String dishId) {
        this.dishId = dishId;
    }
}
