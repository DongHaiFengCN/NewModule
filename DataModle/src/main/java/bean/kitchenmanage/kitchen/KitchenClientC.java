package bean.kitchenmanage.kitchen;

import java.util.List;

/**
 * Class description goes here.
 * <p>
 * Created by loongsun on 2017/5/15.
 * <p>
 * email: 125736964@qq.com
 */

public class KitchenClientC {
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
     * 厨房菜间名称
     */
    private String name;
    /**
     * 厨房地址,在厨房打印机模式下叫打印机名称；在厨房平板模式下是平板ip地址
     */
    private String kitchenAdress;
    /**
     * 关联所属菜品类
     */
    private List<String> dishesKindIDList;
    /**
     * 数据分两大类，一个是基础数据 basic，一个业务实时数据 business
     */
    private String dataType = "BaseData";//basi,代表basic数据,busi,代表业务数据
    /**
     * 打印机序号
     */
    private int indexPrinter;
    /**
     * 是否配置了打印机
     */
    private boolean havePrinter;
    /**
     * 打印机连接状态
     */
    private boolean statePrinter;


    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public KitchenClientC()
    {

    }

    public KitchenClientC(String company_id) {
        this.channelId = company_id;
        this.className="KitchenClientC";
    }

    public int getIndexPrinter() {
        return indexPrinter;
    }

    public void setIndexPrinter(int indexPrinter) {
        this.indexPrinter = indexPrinter;
    }

    public boolean isHavePrinter() {
        return havePrinter;
    }

    public void setHavePrinter(boolean havePrinter) {
        this.havePrinter = havePrinter;
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



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKitchenAdress() {
        return kitchenAdress;
    }

    public void setKitchenAdress(String kitchenAdress) {
        this.kitchenAdress = kitchenAdress;
    }

    public List<String> getDishesKindIDList() {
        return dishesKindIDList;
    }

    public void setDishesKindIDList(List<String> dishesKindIDList) {
        this.dishesKindIDList = dishesKindIDList;
    }
}
