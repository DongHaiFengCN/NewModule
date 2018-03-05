package bean.kitchenmanage.depot;

/**
 * 数量单位 类文件（字典项）
 *
 * Created by loongsun on 17/1/8.
 *
 * email: 125736964@qq.com
 */

public class MaterialUnit {


    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_rev() {
        return _rev;
    }

    public void set_rev(String _rev) {
        this._rev = _rev;
    }
    /**
     * 公司唯一身份id,用于数据同步,做为唯一管道符
     */
    private String channelId;

    private String class_name;
    private String _id;
    private String _rev;

    private String unitName;

    public MaterialUnit(String unitName) {
        this.unitName = unitName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
}
