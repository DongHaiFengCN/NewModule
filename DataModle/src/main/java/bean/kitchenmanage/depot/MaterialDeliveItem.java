package bean.kitchenmanage.depot;

import java.io.Serializable;

/**
 * @ClassName: MaterialDeliveItem
 * @Description: 出库每个源料记录类文件
 * @author loongsun
 * @date 2016-01-01 上午1:19:08
 *
 */
public class MaterialDeliveItem implements Serializable {
    /**
     * 公司唯一身份id,用于数据同步,做为唯一管道符
     */
    private String channelId;
    /**
     * 单位名称
     */
    private String unitName;

    /**
     * 所属的出库记录
     */
    private String materialDelive;
    /**
     * 对应的源料
     */
    private String material;
    /**
     * 出库数量
     */
    private float deliveCount;
    /**
     * 该源料出库价值
     */
    private float totalValue;
    /**
     * 出库数量
     */

    private MaterialStorageItem materialStorageItemList;

    public MaterialDeliveItem() {
    }

/*    public MaterialDeliveItem(MaterialDelive materialDelive, MaterialC material, float deliveCount, float totalValue, MaterialStorageItem materialStorageItemList) {
        this.materialDelive = materialDelive;

        this.deliveCount = deliveCount;
        this.totalValue = totalValue;
        this.materialStorageItemList = materialStorageItemList;
    }*/

/*    public MaterialDelive getMaterialDelive() {
        return materialDelive;
    }

    public void setMaterialDelive(MaterialDelive materialDelive) {
        this.materialDelive = materialDelive;
    }*/

/*    public MaterialC getMaterial() {
        return material;
    }

    public void setMaterial(MaterialC material) {
        this.material = material;
    }*/

    public float getDeliveCount() {
        return deliveCount;
    }

    public void setDeliveCount(float deliveCount) {
        this.deliveCount = deliveCount;
    }

    public float getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(float totalValue) {
        this.totalValue = totalValue;
    }

    public MaterialStorageItem getMaterialStorageItemList() {
        return materialStorageItemList;
    }

    public void setMaterialStorageItemList(MaterialStorageItem materialStorageItemList) {
        this.materialStorageItemList = materialStorageItemList;
    }
}
