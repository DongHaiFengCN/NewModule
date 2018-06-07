package bean.kitchenmanage.promotion;

import java.io.Serializable;

/**
 * Class description ：活动规则类
 *
 * Created by loongsun on 17/1/8.
 *
 * email: 125736964@qq.com
 */
public class PromotionRule implements Serializable {

    /**
     * 满金额
     */
    private int counts;
    /**
     * 折扣或优惠金额
     */
    private int discounts;

    /**
     * 活动类型 1,满折；2，满赠，3满减、4、充赠（用于增加会员时提示充值多少赠送多少）
     */
    private int mode;

    public PromotionRule() {
    }
    public int getCounts() {
        return counts;
    }

    public void setCounts(int counts) {
        this.counts = counts;
    }

    public int getDiscounts() {
        return discounts;
    }

    public void setDiscounts(int discounts) {
        this.discounts = discounts;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
