package tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

/***********************************************************************************************
 * 4、时间格式化
 *********************************************************************************************/

    /**
     * @return 时间格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getFormatDate() {
        Date date = new Date();
        if (date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return formatter.format(date);
        }

        return null;
    }


    public static String getNianDate() {
        final Calendar c = Calendar.getInstance();
        if (c == null){
            return "";
        }
        return ""+c.get(Calendar.YEAR);
    }
}
