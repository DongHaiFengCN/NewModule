package bean.kitchenmanage.depot;

import java.io.Serializable;
import java.util.List;


/**
 * @ClassName: MaterialStorage
 * @Description: 源料入库类文件
 * @author loongsun
 * @date 2016-01-01 上午1:19:08
 *
 */
public class MaterialStorage implements Serializable
{
    /**
     * 入库单号
     */
    private String id;

    /**
     * 公司唯一身份id,用于数据同步,做为唯一管道符
     */
    private String channelId;
    /**
     *入库时间
     */
    private String  storageTime;//
    /**
     *系统操作者
     */
    private String manager;
    /**
     *系统操作者电话
     */
    private String Mobile;
    /**
     *入库总价值
     */
    private float totalValue;
    /**
     *备注
     */
    private String note;
    /**
     *状态 1、草稿；2、提交
     */
    private int state;
    /**
     *该次入库的源料列表
     */

    private List<String> materialStorageItemList;

    public MaterialStorage() {
    }

    public MaterialStorage(String id, String storageTime,  float totalValue, String note, int state, List<MaterialStorageItem> materialStorageItemList) {
        this.id = id;
        this.storageTime = storageTime;
        this.totalValue = totalValue;
        this.note = note;
        this.state = state;
        //this.materialStorageItemList = materialStorageItemList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStorageTime() {
        return storageTime;
    }

    public void setStorageTime(String storageTime) {
        this.storageTime = storageTime;
    }



    public float getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(float totalValue) {
        this.totalValue = totalValue;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

/*    public List<MaterialStorageItem> getMaterialStorageItemList() {
        return materialStorageItemList;
    }

    public void setMaterialStorageItemList(List<MaterialStorageItem> materialStorageItemList) {
        this.materialStorageItemList = materialStorageItemList;
    }*/
  /*  public void addMaterialStorageItem(MaterialStorageItem item)
    {
        if(null==materialStorageItemList)
            materialStorageItemList=new ArrayList<>();
        materialStorageItemList.add(item);
    }*/
}
