package bean.kitchenmanage.depot;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bean.kitchenmanage.user.Employee;

/**
 * @ClassName: Depot
 * @Description: 仓库类文件
 * @author loongsun
 * @date 2016-01-01 上午1:19:08
 *
 */
public class DepotC implements Serializable {

    private String class_name;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    private String _id;

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String get_rev() {
        return _rev;
    }

    public void set_rev(String _rev) {
        this._rev = _rev;
    }


    private String _rev;
    /**
     * 公司唯一身份id,用于数据同步,做为唯一管道符
     */
    private String channelId;
    /**
     * 仓库名称
     */
    private String depotName;
    /**
     * 负责人
     */
    private Employee manager;
    /**
     * 创建者
     */
    private Employee creator;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 出库方式：1，手机点餐自动出库；2，手动点餐出库
     */
    private int deliveMode;
    /**
     * 所包含源料
     */
    private List<MaterialC> materialList;

    public DepotC()
    {
    }
    public DepotC(String depotName, Employee manager, Employee creator, Date createdTime, int deliveMode)
    {
        this.depotName = depotName;
        this.manager = manager;
        this.creator = creator;
        this.createdTime = createdTime;
        this.deliveMode = deliveMode;
    }

    public int getDeliveMode() {
        return deliveMode;
    }

    public void setDeliveMode(int deliveMode) {
        this.deliveMode = deliveMode;
    }

    public String getDepotName() {
        return depotName;
    }

    public void setDepotName(String depotName) {
        this.depotName = depotName;
    }

/*    public Users getManager() {
        return manager;
    }

    public void setManager(Users manager) {
        this.manager = manager;
    }

    public Users getCreator() {
        return creator;
    }

    public void setCreator(Users creator) {
        this.creator = creator;
    }*/

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public List<MaterialC> getMaterialList() {
        return materialList;
    }

    public void setMaterialList(List<MaterialC> materialList) {
        this.materialList = materialList;
    }
    public void addMateria(MaterialC obj)
    {
        if(null==materialList)
            materialList=new ArrayList<MaterialC>();
        materialList.add(obj);
    }
}
