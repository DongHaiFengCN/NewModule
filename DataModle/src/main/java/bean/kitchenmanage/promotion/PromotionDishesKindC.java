package bean.kitchenmanage.promotion;

import java.util.ArrayList;
import java.util.List;

/**
 * Class description ：活动关联菜品品类
 *
 * Created by loongsun on 17/1/8.
 *
 * email: 125736964@qq.com
 */
public class PromotionDishesKindC {
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
     * 菜品类id
     */
    private String dishesKindId;
    /**
     *是否选择 ，0，未选择，1，选择
     */
    private  int ischecked;
    /**
     *
     */
    private List<PromotionDishesC> promotionDishesList;

    public PromotionDishesKindC() {
    }

    public PromotionDishesKindC(String company_id) {
        this.channelId = company_id;
        this.className="PromotionDishesKindC";
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

    public String getDishesKindId() {
        return dishesKindId;
    }

    public void setDishesKindId(String dishesKindId) {
        this.dishesKindId = dishesKindId;
    }

    public int getIschecked() {
        return ischecked;
    }

    public void setIschecked(int ischecked) {
        this.ischecked = ischecked;
    }

    public List<PromotionDishesC> getPromotionDishesList() {
        return promotionDishesList;
    }

    public void setPromotionDishesList(List<PromotionDishesC> promotionDishesList) {
        this.promotionDishesList = promotionDishesList;
    }

    public void addPromotionDishes(PromotionDishesC obj)
    {
        if(this.promotionDishesList==null)
            this.promotionDishesList=new ArrayList<PromotionDishesC>();
        this.promotionDishesList.add(obj);
    }
}
