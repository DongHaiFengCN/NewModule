package bean.kitchenmanage.order;

/**
 * Class description goes here.
 *
 * @author loongsun
 * Created  on 2018/5/22.
 * <p>
 * email: 125736964@qq.com
 */
public class PromotionDiscountDetail {
    /**
     * 1 打折优惠、2、满减优惠
     */

    private int type;
    /**
     * 优惠了金额
     */
    private float discounts;

    public PromotionDiscountDetail() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getDiscounts() {
        return discounts;
    }

    public void setDiscounts(float discounts) {
        this.discounts = discounts;
    }
}
