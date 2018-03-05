package bean.kitchenmanage.depot;

/**
 * 供应商.
 *
 * Created by loongsun on 17/1/8.
 *
 * email: 125736964@qq.com
 */

public class Provider {
    /**
     * 公司名称
     */
    private String name;
    /**
     * 电话
     */
    private String tel;
    /**
     *  地址
     */
    private String address;
    /**
     * 联系人
     */
    private String contactor;

    public Provider() {
    }

    public Provider(String name, String tel, String contactor, String address) {
        this.name = name;
        this.tel = tel;
        this.contactor = contactor;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getContactor() {
        return contactor;
    }

    public void setContactor(String contactor) {
        this.contactor = contactor;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
