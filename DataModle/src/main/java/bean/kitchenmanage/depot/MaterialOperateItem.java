package bean.kitchenmanage.depot;

import java.io.Serializable;

/**
 * @ClassName: MaterialStorageItem
 * @Description: 每个源料入库记录
 * @author loongsun
 * @date 2016-01-01 上午1:19:08
 *
 */
public class MaterialOperateItem implements Serializable
{

    /**
     *对应的源料入库前对象
     */
    private Material material;
    /**
     *该次入库价格
     */
    private float price;
    /**
     *入库数量
     */
    private float count;
    /**
     *供应商
     */
    private String providerId;

    public MaterialOperateItem() {
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

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

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

}
