package smartkitchen.com.login.model;

/**
 * Class description goes here.
 * <p>
 * Created by loongsun on 2018/1/31.
 * <p>
 * email: 125736964@qq.com
 */

public class responseModle {
    private String statusCode;
    private String message;
    private String data;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
