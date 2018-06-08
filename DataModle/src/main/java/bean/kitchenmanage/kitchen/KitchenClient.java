package bean.kitchenmanage.kitchen;

import java.util.List;

/**
 * 打印机连接类或者厨房连接类，连接终终信息有独特性，不与其它终端通用，只存在终端数据库中，不同步到服务器
 * <p>
 * Created by loongsun on 2017/5/15.
 * <p>
 * email: 125736964@qq.com
 */

public class KitchenClient {

    /**
     * docId
     */
    private String id;
    /**
     * 公司唯一身份id,用于数据同步,做为唯一管道符
     */
    private String channelId;

    /**
     * 数据分两大类，一个是基础数据 BaseData，一个业务实时数据 UserData
     */
    private String dataType = "BaseData";
    /**
     * 类名称
     */
    private String className="KitchenClient";
    /**
     * 厨房菜间名称
     */
    private String name;
    /**
     * 关联所属菜品类
     */
    private List<String> kindIds;
    /**
     * 厨房地址,在厨房打印机模式下叫打印机名称；在厨房平板模式下是平板ip地址
     */
    private String ip;
    /**
     * 打印机序号
     */
    private int printerId;
    /**
     * 打印机状态
     */
    private boolean statePrinter;

    public KitchenClient()
    {

    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getKindIds() {
        return kindIds;
    }

    public void setKindIds(List<String> kindIds) {
        this.kindIds = kindIds;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPrinterId() {
        return printerId;
    }
    public void setPrinterId(int printerId) {
        this.printerId = printerId;
    }

    public boolean isStatePrinter() {
        return statePrinter;
    }

    public void setStatePrinter(boolean statePrinter) {
        this.statePrinter = statePrinter;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
