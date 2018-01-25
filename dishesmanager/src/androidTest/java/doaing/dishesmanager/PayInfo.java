package doaing.dishesmanager;

import java.io.File;

/**
 * 项目名称：AlipayTwo
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/12/1 14:53
 * 修改人：donghaifeng
 * 修改时间：2017/12/1 14:53
 * 修改备注：
 */


public class PayInfo {


    String seller_id;

    String auth_code;



    String out_trade_no;


    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public String getAuth_code() {
        return auth_code;
    }

    public void setAuth_code(String auth_code) {
        this.auth_code = auth_code;
    }
    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

}
