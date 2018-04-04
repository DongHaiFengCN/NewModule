package bean.kitchenmanage.promotion;

import java.util.List;

/**
 * Class description ：活动关联菜品品类
 *
 * Created by loongsun on 17/1/8.
 *
 * email: 125736964@qq.com
 */
public class PromotionDishesKind {
    /**
     * 菜品类id
     */
    private String dishesKindId;
    /**
     *是否选择 ，false，未选择，true，选择
     */
    private  boolean checked;
    /**
     *
     */
    private List<PromotionDishes> promotionDishesList;

    public PromotionDishesKind() {
    }

    public String getDishesKindId() {
        return dishesKindId;
    }

    public void setDishesKindId(String dishesKindId) {
        this.dishesKindId = dishesKindId;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public List<PromotionDishes> getPromotionDishesList() {
        return promotionDishesList;
    }

    public void setPromotionDishesList(List<PromotionDishes> promotionDishesList) {
        this.promotionDishesList = promotionDishesList;
    }
}
