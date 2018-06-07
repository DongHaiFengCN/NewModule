package bean.kitchenmanage.depot;

import java.io.Serializable;
import java.util.List;

import bean.kitchenmanage.user.Employee;


/**
 * @ClassName: MaterialStorage
 * @Description: 源料入库类文件
 * @author loongsun
 * @date 2016-01-01 上午1:19:08
 *
 */
public class MaterialOperate implements Serializable
{
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
    private String className = "MaterialOperate";
    /**
     * 数据分两大类，一个是基础数据 BaseData，一个业务实时数据 UserData
     */
    private String dataType = "BaseData";
    /**
     * 入库单号
     */
    private String num;
    /**
     *入库时间
     */
    private String  createdTime;
    /**
     *系统操作者
     */
    private Employee operator;
    /**
     * 申请者
     */
    private Employee applicant;
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
    private List<MaterialOperateItem> materialStorageItemList;
    /**
     * 入库是1、出库是2
     */
    private int mode;

    public MaterialOperate() {
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

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public Employee getOperator() {
        return operator;
    }

    public void setOperator(Employee operator) {
        this.operator = operator;
    }

    public Employee getApplicant() {
        return applicant;
    }

    public void setApplicant(Employee applicant) {
        this.applicant = applicant;
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

    public List<MaterialOperateItem> getMaterialStorageItemList() {
        return materialStorageItemList;
    }

    public void setMaterialStorageItemList(List<MaterialOperateItem> materialStorageItemList) {
        this.materialStorageItemList = materialStorageItemList;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
