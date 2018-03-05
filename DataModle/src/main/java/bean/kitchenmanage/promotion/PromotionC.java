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
public class PromotionC {
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
     *  活动方式 1.打折；2、赠卷
     */
    private int promotionType;
    /**
     *活动名称
     */
    private String promotionName;

    /**
     *开始时间
     */
    private String startTime;
    /**
     *结束时间
     */
    private String endTime;
    /**
     *所属年份活动 格式：2017
     */
    private String year;
    /**
     *计算方式，1“消费金额” 2“菜品金额”
     */
    private int  countMode;
    /**
     *支持的消费规则，比如是消费多少打几折，还是消费多少优惠多少
     */
    private List <PromotionRuleC> promotionRuleList;
    /**
     *所支持的菜品品类
     */
    private List<PromotionDishesKindC> promotionDishesKindList;

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


    public PromotionC() {
    }

    public PromotionC(String company_id) {
        this.channelId = company_id;
        className="PromotionC";

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




    public int getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(int promotionType) {
        this.promotionType = promotionType;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getCountMode() {
        return countMode;
    }

    public void setCountMode(int countMode) {
        this.countMode = countMode;
    }

    public List<PromotionRuleC> getPromotionRuleList() {
        return promotionRuleList;
    }

    public void setPromotionRuleList(List<PromotionRuleC> promotionRuleList) {
        this.promotionRuleList = promotionRuleList;
    }

    public List<PromotionDishesKindC> getPromotionDishesKindList() {
        return promotionDishesKindList;
    }

    public void setPromotionDishesKindList(List<PromotionDishesKindC> promotionDishesKindList) {
        this.promotionDishesKindList = promotionDishesKindList;
    }

    public void addPromotionRule(PromotionRuleC obj)
    {
        if(this.promotionRuleList==null)
            this.promotionRuleList=new ArrayList<PromotionRuleC>();
        this.promotionRuleList.add(obj);
    }

    public void addPromotionDishesKind(PromotionDishesKindC obj)
    {
        if(this.promotionDishesKindList==null)
            this.promotionDishesKindList=new ArrayList<PromotionDishesKindC>();
        this.promotionDishesKindList.add(obj);
    }
}
