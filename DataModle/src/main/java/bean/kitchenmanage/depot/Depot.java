package bean.kitchenmanage.depot;

/**
 * Class description goes here.
 *
 * @author loongsun
 * Created  on 2018/5/15.
 * <p>
 * email: 125736964@qq.com
 */
public class Depot {
    /**
     *对象id,等于docmentid,一般用于Pojo操作时使用。
     */
    private  String id;
    /**
     * 公司唯一身份id,用于数据同步,做为唯一管道符
     */
    private String channelId;
    /**
     * 类名，用于数据库查询类过滤
     */
    private String className = "Depot";
    /**
     * 数据分两大类，一个是基础数据 BaseData，一个业务实时数据 UserData
     */
    private String dataType = "BaseData";
    /**
     * 仓库名称
     */
    private String name;
    /**
     * 创建时间
     */
    private String createdTime;
    /**
     * 出库方式：1，点餐出库；2，人工出库
     */
    private int mode;

    public Depot() {
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

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
