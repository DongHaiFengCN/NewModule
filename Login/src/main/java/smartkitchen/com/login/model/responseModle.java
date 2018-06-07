package smartkitchen.com.login.model;

/**
 * Class description goes here.
 * <p>
 * Created by loongsun on 2018/1/31.
 * <p>
 * email: 125736964@qq.com
 */

public class responseModle
{
    private String status;
    private String msg;
    private channleData data;

    public responseModle() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public channleData getData() {
        return data;
    }

    public void setData(channleData data) {
        this.data = data;
    }
}
