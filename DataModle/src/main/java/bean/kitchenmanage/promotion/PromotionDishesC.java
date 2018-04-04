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
     *菜品id
     */
    private String dishesId;
    /**
     * 活动自定义价格
     */
    private float promotionPrice;
    /**
     *是否选择 ，false，未选择，true，选择
     */
    private boolean checked;

    public PromotionDishesC() {
    }

    public String getDishesId() {
        return dishesId;
    }

    public void setDishesId(String dishesId) {
        this.dishesId = dishesId;
    }

    public float getPromotionPrice() {
        return promotionPrice;
    }

    public void setPromotionPrice(float promotionPrice) {
        this.promotionPrice = promotionPrice;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
