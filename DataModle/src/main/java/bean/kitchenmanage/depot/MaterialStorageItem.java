package bean.kitchenmanage.depot;

import java.io.Serializable;

/**
 * @ClassName: MaterialStorageItem
 * @Description: 每个源料入库记录
 * @author loongsun
 * @date 2016-01-01 上午1:19:08
 *
 */
public class MaterialStorageItem implements Serializable
{
    /**
     * 公司唯一身份id,用于数据同步,做为唯一管道符
     */
    private String channelId;
    /**
     * 所属的入库记录
     */
    private String materialStorage;
    /**
     *对应的源料
     */
    private String material;
    /**
     *该次入库价格
     */
    private float price;
    /**
     *入库数量
     */
    private float count;
    /**
     *剩余数量，入库时与count相等，出库时在此进行相减操作
     */
    private float remainder;
    /**
     *供应商
     */
    private String provider;

    public MaterialStorageItem() {
    }

/*    public MaterialStorageItem(MaterialStorage materialStorage, MaterialC material, float price, float count, float remainder, Provider provider) {
        this.materialStorage = materialStorage;
        this.material = material;
        this.price = price;
        this.count = count;
        this.remainder = remainder;
        this.provider = provider;
    }

    public MaterialStorage getMaterialStorage() {
        return materialStorage;
    }

    public void setMaterialStorage(MaterialStorage materialStorage) {
        this.materialStorage = materialStorage;
    }

    public MaterialC getMaterial() {
        return material;
    }

    public void setMaterial(MaterialC material) {
        this.material = material;
    }*/

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getCount() {
        return count;
    }

    public void setCount(float count) {
        this.count = count;
    }




}
