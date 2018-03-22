package bean.kitchenmanage.order;


import java.util.ArrayList;
import java.util.List;

import bean.kitchenmanage.member.MembersC;
import bean.kitchenmanage.promotion.PromotionC;

/**
 * Class description ：营销方式下的支付详情类
 * <p>
 * Created by loongsun on 17/1/8.
 * <p>
 * email: 125736964@qq.com
 */

public class PromotionDetailC {
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
     * 营销方式
     */
    private PromotionTypeC promotionType;
    /**
     * 支付细节
     */
    private List<PayDetailC> payDetailList;
    /**
     * 折扣
     */
    private int disrate;
    /**
     * 优惠金额
     */
    private float discounts;
    /**
     * 会员
     */
    private MembersC members;
    /**
     * 促销活动
     */
    private PromotionC promotion;
    /**
     * 只有业务数据才有年份划分:2018
     */
    private String createdYear;
    /**
     * 数据分两大类，一个是基础数据 basic，一个业务实时数据 business
     */
    private String dataType = "UserData";

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getCreatedYear() {
        return createdYear;
    }

    public void setCreatedYear(String createdYear) {
        this.createdYear = createdYear;
    }


    public PromotionDetailC()
    {
    }

    public PromotionDetailC(String company_id) {
        this.channelId = company_id;
        this.className="PromotionDetailC";
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

    public PromotionTypeC getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(PromotionTypeC promotionType) {
        this.promotionType = promotionType;
    }

    public List<PayDetailC> getPayDetailList() {
        return payDetailList;
    }

    public void setPayDetailList(List<PayDetailC> payDetailList) {
        this.payDetailList = payDetailList;
    }
    public  void addPayDetail(PayDetailC obj)
    {
        if(this.payDetailList==null)
            this.payDetailList=new ArrayList<PayDetailC>();
        this.payDetailList.add(obj);
    }

    public int getDisrate() {
        return disrate;
    }

    public void setDisrate(int disrate) {
        this.disrate = disrate;
    }

    public float getDiscounts() {
        return discounts;
    }

    public void setDiscounts(float discounts) {
        this.discounts = discounts;
    }

    public MembersC getMembers() {
        return members;
    }

    public void setMembers(MembersC members) {
        this.members = members;
    }

    public PromotionC getPromotion() {
        return promotion;
    }

    public void setPromotion(PromotionC promotion) {
        this.promotion = promotion;
    }
}
