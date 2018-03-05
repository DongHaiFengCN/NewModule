package bean.kitchenmanage.order;

import java.util.List;

/**
 * Class description ：促销方式类
 * <p>
 * Created by loongsun on 17/1/8.
 * <p>
 * email: 125736964@qq.com
 */

public class PromotionTypeC {
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
     * 促销类型 1、会员；2、促销活动；3、任意打折；4、不进行任何促销
     */
    private int type;
    /**
     * 每种促销类型下支持的支付方式
     */
    private List<PayTypeC> payTypeList;

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


    public PromotionTypeC() {
    }

    public PromotionTypeC(String company_id) {
        this.channelId = company_id;
        this.className="PromotionTypeC";
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<PayTypeC> getPayTypeList() {
        return payTypeList;
    }

    public void setPayTypeList(List<PayTypeC> payTypeList) {
        this.payTypeList = payTypeList;
    }
}
