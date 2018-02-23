package tools;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 共公方法
 * <p>
 * Created by loongsun on 2018/1/17.
 * <p>
 * email: 125736964@qq.com
 */

public class Method {

    public static String getNewFormatDate(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }
    public static String getStrTime(Date date)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

}
