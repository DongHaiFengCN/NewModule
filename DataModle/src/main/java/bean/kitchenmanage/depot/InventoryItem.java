package bean.kitchenmanage.depot;

import java.io.Serializable;

/**
 * @ClassName: InventoryItem
 * @Description: 盘点源料类项
 * @author loongsun
 * @date 2016-01-01 上午1:19:08
 *
 */
public class InventoryItem implements Serializable {

    /**
     * 盘点的源料
     */
    private Material material;
    /**
     * 理论剩余源料量
     */
    private float remainder;//仓库记录数量
    /**
     * 实际盘点剩余源料量
     */
    private float count;
    /**
     * 偏差量
     */
    private float compare;//盘点差异数

    public InventoryItem() {
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public float getRemainder() {
        return remainder;
    }

    public void setRemainder(float remainder) {
        this.remainder = remainder;
    }

    public float getCount() {
        return count;
    }

    public void setCount(float count) {
        this.count = count;
    }

    public float getCompare() {
        return compare;
    }

    public void setCompare(float compare) {
        this.compare = compare;
    }
}
