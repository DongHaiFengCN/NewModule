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

public class CardTypeC {
    /**
     * 公司唯一身份id,用于数据同步,做为唯一管道符
     */
    private String channelId;
    /**
     * 类名，用于数据库查询类过滤
     */
    private String className;
    /**
     * docId
     */
    private String _id;
    /**
     *
     */
    private int cardType;//1,折扣卡,2,充赠卡
    /**
     * 卡名称
     */
    private String cardName;
    /**
     * 折扣率
     */
    private int disrate;
    /**
     * 实际充值金额
     */
    private int recharge;
    /**
     * 赠送金额
     */
    private int rechargeGive;
    /**
     *所关联的菜品品类
     */
    private List<CardDishesKindC> cardDishesKindList;
    /**
     *是否有效，1有效，0，无效
     */
    private int valite;
    /**
     * 数据分两大类，一个是基础数据 basic，一个业务实时数据 business
     */
    private String dataType = "BaseData";//basi,代表basic数据,busi,代表业务数据

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public CardTypeC()
    {
    }
    public CardTypeC(String company_id )
    {
        this.channelId=company_id;
        this.className="CardTypeC";
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

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }


    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public int getDisrate() {
        return disrate;
    }

    public void setDisrate(int disrate) {
        this.disrate = disrate;
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

    public List<CardDishesKindC> getCardDishesKindList() {
        return cardDishesKindList;
    }

    public void setCardDishesKindList(List<CardDishesKindC> cardDishesKindList) {
        this.cardDishesKindList = cardDishesKindList;
    }

    public int getValite() {
        return valite;
    }

    public void setValite(int valite) {
        this.valite = valite;
    }

    public void addCardDishesKind(CardDishesKindC obj)
    {
        if(cardDishesKindList==null)
            cardDishesKindList=new ArrayList<CardDishesKindC>();
        cardDishesKindList.add(obj);
    }
}
