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

    static final String TAG ="DOAING";

    static boolean FLAG =true;

    public static void e(String info){


        if(FLAG){

            android.util.Log.e(TAG,info);

        }

    }
    public static void d(String info){


        if(FLAG){

            android.util.Log.d(TAG,info);

        }

    }
    public static void e(String tag,String content)
    {
        if(FLAG){
            android.util.Log.e(tag,content);
        }
    }
    public static void e(String tag,String content, Throwable tr)
    {
        if(FLAG){
            android.util.Log.e(tag,content,tr);
        }
    }
    public static void d(String tag,String content)
    {
        if(FLAG){
            android.util.Log.d(tag,content);
        }
    }
}
