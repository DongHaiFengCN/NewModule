package bean.kitchenmanage.order;

/**
 * Class 忌口类.
 * <p>
 * Created by loongsun on 17/1/7.
 * <p>
 * email: 125736964@qq.com
 */

public class TaboosC {
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
     * 忌口描述
     */
    private String name;
    /**
     * 是否有效 1，有效，0无效
     */
    private int valid;

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


    public TaboosC() {
    }

    public TaboosC(String company_id, String name, int isValid) {
        this.channelId = company_id;
        this.name = name;
        this.valid = isValid;
        this.className="TaboosC";
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValid() {
        return valid;
    }

    public void setValid(int valid) {
        this.valid = valid;
    }
}
