package bean.kitchenmanage.promotion;

import java.util.ArrayList;
import java.util.List;

/**
 * Class description ：营销活动类
 *
 * Created by loongsun on 17/1/8.
 *
 * email: 125736964@qq.com
 */
public class Promotion {
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
     *活动名称
     */
    private String name;
    /**
     *开始时间
     */
    private String startTime;
    /**
     *结束时间
     */
    private String endTime;
    /**
     *支持的消费规则，比如是消费多少打几折，还是消费多少优惠多少
     */
    private List <String> promotionRuleIds;
    /**
     *所支持的菜品品类
     */
    private List<PromotionDishesKindC> promotionDishesKindList;


    public Promotion() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<String> getPromotionRuleIds() {
        return promotionRuleIds;
    }

    public void setPromotionRuleIds(List<String> promotionRuleIds) {
        this.promotionRuleIds = promotionRuleIds;
    }

    public List<PromotionDishesKindC> getPromotionDishesKindList() {
        return promotionDishesKindList;
    }

    public void setPromotionDishesKindList(List<PromotionDishesKindC> promotionDishesKindList) {
        this.promotionDishesKindList = promotionDishesKindList;
    }
}
