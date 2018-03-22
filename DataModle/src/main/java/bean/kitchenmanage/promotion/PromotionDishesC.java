package bean.kitchenmanage.promotion;

/**
 * Class description ：活动关联的菜品
 *
 * Created by loongsun on 17/1/8.
 *
 * email: 125736964@qq.com
 */
public class PromotionDishesC {
    /**
     * 公司唯一身份id,用于数据同步,做为唯一管道符
     */
    private String channelId;
    /**
     * 类名，用于数据库查询类过滤
     */
    private String className;
    /**
     *
     * docId
     */
    private String _id;

    /**
     *菜品id
     */
    private String dishesId;

    /**
     *是否选择 ，0，未选择，1，选择
     */
    private int ischecked;

    public PromotionDishesC() {
    }

    public PromotionDishesC(String company_id) {
        this.channelId = company_id;
        this.className="PromotionDishesC";
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

    public String getDishesId() {
        return dishesId;
    }

    public void setDishesId(String dishesId) {
        this.dishesId = dishesId;
    }

    public int getIschecked() {
        return ischecked;
    }

    public void setIschecked(int ischecked) {
        this.ischecked = ischecked;
    }
}
