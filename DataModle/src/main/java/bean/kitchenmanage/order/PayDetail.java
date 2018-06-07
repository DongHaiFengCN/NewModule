package bean.kitchenmanage.order;

/**
 * Class 支付详情类
 *
 * Created by loongsun on 17/1/7.
 *
 * email: 125736964@qq.com
 */

public class PayDetail
{
    /**
     * 支付方式 支付类型名称 1\现金、2\银行卡、3\微信、4\支付宝、5\美团、6\会员卡 7\抹零 8\赠卷 9\饿了吗 10\挂账 11\团购
     */
    private int payTypes;


    /**
     * 现金
     */
    public final static int PAYTYPE_CASH = 1;

    /**
     * 银联
     */
    public final static int PAYPYTE_BANK = 2;


    /**
     * 微信支付
     */
    public final static int PAYPYTE_WECHAT = 3;

    /**
     * 支付宝支付
     */
    public final static int  PAYPYTE_ALIPAY= 4;

    /**
     * 美团支付
     */
    public final static int PAYPYTE_MEITU = 5;

    /**
     * 会员支付
     */
    public final static int PAYPYTE_MEMBER = 6;

    /**
     * 抹零支付
     */
    public final static int PAYPYTE_WIPE = 7;

    /**
     * 赠券支付
     */
    public final static int PAYPYTE_COUPON = 8;

    /**
     * 饿了么支付
     */
    public final static int PAYPYTE_ELEME = 9;

    /**
     * 挂账支付
     */
    public final static int PAYPYTE_HANG = 10;

    /**
     * 挂账支付
     */
    public final static int PAYPYTE_TEAMBUY = 11;


    /**
     * 支付金额
     */
    private float subtotal;

    /**
     * 支付产生的时间，用于统计
     */
    private String createdTime;

    public PayDetail() {
    }

    public int getPayTypes() {
        return payTypes;
    }

    public void setPayTypes(int payTypes) {
        this.payTypes = payTypes;
    }

    public float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(float subtotal) {
        this.subtotal = subtotal;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }
}
