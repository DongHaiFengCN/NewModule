package bean.kitchenmanage.order;

/**
 * 流水号
 * <p>
 * Created by loongsun on 2017/11/28.
 * <p>
 * email: 125736964@qq.com
 */

public class OrderNum {

    //1、
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
     *yyyy-MM-DD
     */
    private String Date;
    /**
     * 从1开始三位数
     */
    private int num;
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

    public OrderNum() {
    }

    public OrderNum(String company_id)
    {
        this.channelId = company_id;
        this.className="OrderNum";
    }
    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
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
}
