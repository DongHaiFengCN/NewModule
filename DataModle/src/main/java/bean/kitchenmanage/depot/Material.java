package bean.kitchenmanage.depot;

import java.io.Serializable;
import java.util.List;

/**
 * Class description goes here.
 *
 * @author loongsun
 * Created  on 2018/5/15.
 * <p>
 * email: 125736964@qq.com
 */
public class Material implements Serializable {
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
    private String className = "Material";
    /**
     * 数据分两大类，一个是基础数据 BaseData，一个业务实时数据 UserData
     */
    private String dataType = "BaseData";
    /**
     * 原料名称
     */
    private String name;
    /**
     *名称首字母
     */
    private String code;

    /**
     * 单位类型Id（要有维护单位数据的地方）
     */
    private String unitId;
    /**
     * 食材类型Id（要有维护食材类型的地方）
     */
    private String kindId;
    /**
     *存放的仓库
     */
    private String depotId;//
    /**
     *库存余量
     */
    private float stock;
    /**
     *预警库存量
     */
    private float stockAlert;//
    /**
     * 菜品对应消耗量
     */
    private List<DishConsum> dishesConsumList;

    /**
     * 所对应的源料供应商
     */
    private List<String> providerIdList;

    public Material() {
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getKindId() {
        return kindId;
    }

    public void setKindId(String kindId) {
        this.kindId = kindId;
    }

    public String getDepotId() {
        return depotId;
    }

    public void setDepotId(String depotId) {
        this.depotId = depotId;
    }

    public float getStock() {
        return stock;
    }

    public void setStock(float stock) {
        this.stock = stock;
    }

    public float getStockAlert() {
        return stockAlert;
    }

    public void setStockAlert(float stockAlert) {
        this.stockAlert = stockAlert;
    }

    public List<DishConsum> getDishesConsumList() {
        return dishesConsumList;
    }

    public void setDishesConsumList(List<DishConsum> dishesConsumList) {
        this.dishesConsumList = dishesConsumList;
    }

    public List<String> getProviderIdList() {
        return providerIdList;
    }

    public void setProviderIdList(List<String> providerIdList) {
        this.providerIdList = providerIdList;
    }
}
