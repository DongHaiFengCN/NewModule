package bean.kitchenmanage.depot;

/**
 * @ClassName: DishesConsume
 * @Description: 菜品消费量
 * @author loongsun
 * @date 2016-01-01 上午1:19:08
 *
 */
public class DishesConsumeC {


    /**
     * 公司唯一身份id,用于数据同步,做为唯一管道符
     */
    private String channelId;
    /**
     * 菜品
     */
    private String dishesId;

    /**
     * 消耗数量
     */
    private float consums;

    /**
     * 原材料id
     */
     private String materialId;

    public DishesConsumeC()
    {
    }


}
