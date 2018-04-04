package bean.kitchenmanage.member;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: CardType
 * @Description: 会员卡类型
 * @author loongsun
 * @date 2014-7-29 上午1:06:02
 *
 */

public class CardType {
    /**
     * 公司唯一身份id,用于数据同步,做为唯一管道符
     */
    private String channelId;
    /**
     * 类名，用于数据库查询类过滤
     */
    private String className = "CardType";
    /**
     * docId
     */
    private String id;
    /**
     * 数据分两大类，一个是基础数据 BaseData，一个业务实时数据 UserData
     */
    private String dataType = "BaseData";
    /**
     * 卡名称
     */
    private String name;
    /**
     * 充值金额
     */
    private int recharge;
    /**
     * 赠送金额
     */
    private int rechargeGive;
    /**
     *是否有效， false 无效：不能开新卡，不参加活动，老会员仍然能用，仍然也能充值
     * 但卡类列表仍然显示
     */
    private boolean valid;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public CardType()
    {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRecharge() {
        return recharge;
    }

    public void setRecharge(int recharge) {
        this.recharge = recharge;
    }

    public int getRechargeGive() {
        return rechargeGive;
    }

    public void setRechargeGive(int rechargeGive) {
        this.rechargeGive = rechargeGive;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
