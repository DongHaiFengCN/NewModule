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
     * 公司唯一身份id,用于数据同步,做为唯一管道符
     */
    private String channelId;
    /**
     * 所属的盘点类
     */
    private String  inventory;
    /**
     * 盘点的源料
     */
    private String material;
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

    public InventoryItem(Inventory inventory, MaterialC material, float remainder, float count, float compare) {
        this.remainder = remainder;
        this.count = count;
        this.compare = compare;
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
