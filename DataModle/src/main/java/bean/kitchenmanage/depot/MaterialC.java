package bean.kitchenmanage.depot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: Material
 * @Description: 源料类文件
 * @author loongsun
 * @date 2016-01-01 上午1:19:08
 *
 */
public class MaterialC implements Serializable
{
    /**
     * 原料名称
     */
    private String name;
    /**
     *名称首字母
     */
    private String code;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getDepot() {
        return depot;
    }

    public void setDepot(String depot) {
        this.depot = depot;
    }
    public String price;
    /**
     * 公司唯一身份id,用于数据同步,做为唯一管道符
     */
    private String channelId;
    /**
     * 数量单位（字典项）
     */
    private String unit;

    /**
     * 食材类型（字典项）
     */
    private String kind;//
    /**
     *存放的仓库
     */
    private String depot;//
    /**
     *库存量
     */
    private float stock;
    /**
     *预警库存量
     */
    private float stockAlert;//
    /**
     *该源料的入库记录
     */
    private List<MaterialStorageItem> materialStorageItemList;
    /**
     *该源料的出库记录
     */
    private List<MaterialDeliveItem> materialDeliveItemList;

    /**
     * 菜品对应消耗量
     */
    private List<String> dishesConsumeList;


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



    public float getStock() {
        return stock;
    }

    public void setStock(float stock) {
        this.stock = stock;
    }

    public float getStockAlert() {
        return stockAlert;
    }

    public void setStockAlert(float stockAlert)
    {
        this.stockAlert = stockAlert;
    }

    public List<MaterialStorageItem> getMaterialStorageItemList() {
        return materialStorageItemList;
    }

    public void setMaterialStorageItemList(List<MaterialStorageItem> materialStorageItemList) {
        this.materialStorageItemList = materialStorageItemList;
    }
    public void addMaterialStorageItem(MaterialStorageItem item)
    {
        if(null==materialStorageItemList)
            materialStorageItemList=new ArrayList<MaterialStorageItem>();
        materialStorageItemList.add(item);
    }

    public List<MaterialDeliveItem> getMaterialDeliveItemList() {
        return materialDeliveItemList;
    }

    public void setMaterialDeliveItemList(List<MaterialDeliveItem> materialDeliveItemList) {
        this.materialDeliveItemList = materialDeliveItemList;
    }
    public void addMaterialDeliveItem(MaterialDeliveItem item)
    {
        if(null==materialDeliveItemList)
            materialDeliveItemList=new ArrayList<MaterialDeliveItem>();
        materialDeliveItemList.add(item);
    }


}
