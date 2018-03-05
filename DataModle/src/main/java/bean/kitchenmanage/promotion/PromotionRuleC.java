package bean.kitchenmanage.promotion;

/**
 * Class description ：活动规则类
 *
 * Created by loongsun on 17/1/8.
 *
 * email: 125736964@qq.com
 */
public class PromotionRuleC {

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
     * 消费金额
     */
    private int counts;
    /**
     * 折扣或优惠金额
     */
    private int discounts;

    /**
     * 所属活动
     */
    private String promotionId;

    /**
     * 活动类型 1,打折；2，赠券
     */
    private int promotionType;

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


    public PromotionRuleC() {
    }

    public PromotionRuleC(String company_id)
    {
        this.channelId = company_id;
        this.className ="PromotionRuleC";
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

    public String getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(String promotionId) {
        this.promotionId = promotionId;
    }

    public int getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(int promotionType) {
        this.promotionType = promotionType;
    }
}
