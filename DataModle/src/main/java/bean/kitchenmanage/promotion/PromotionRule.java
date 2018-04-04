package bean.kitchenmanage.promotion;

/**
 * Class description ：活动规则类
 *
 * Created by loongsun on 17/1/8.
 *
 * email: 125736964@qq.com
 */
public class PromotionRule {
    /**
     * docId
     */
    private String id;
    /**
     * 公司唯一身份id,用于数据同步,做为唯一管道符
     */
    private String channelId;
    /**
     * 类名，用于数据库查询类过滤
     */
    private String className = "Promotion";
    /**
     * 数据分两大类，一个是基础数据 BaseData，一个业务实时数据 UserData
     */
    private String dataType = "BaseData";
    /**
     * 满金额
     */
    private int counts;
    /**
     * 折扣或优惠金额
     */
    private int discounts;

    /**
     * 活动类型 1,打折；2，满赠，3满减
     */
    private int mode;

    public PromotionRule() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
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
