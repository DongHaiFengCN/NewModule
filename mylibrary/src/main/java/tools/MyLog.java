package tools;

import android.util.Log;

/**
 * 项目名称：dlcache
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2018/1/15 11:51
 * 修改人：donghaifeng
 * 修改时间：2018/1/15 11:51
 * 修改备注：
 */

public class MyLog  {

    private static boolean isDebug = true ;
    public static void e(String info){

        if(isDebug){

            Log.e("DOAING",info);
        }
    }
}
