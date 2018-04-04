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
     * 支付金额
     */
    private float subtotal;

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
}
