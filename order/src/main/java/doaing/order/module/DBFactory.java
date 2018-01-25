package doaing.order.module;

import android.content.Context;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/8 14:58
 * 修改人：donghaifeng
 * 修改时间：2017/9/8 14:58
 * 修改备注：
 */

public class DBFactory {


    public static IDBManager get(DatabaseSource name, Context context){

        IDBManager i = null;

        switch (name){



            case SQLite:

                break;
            case MySQL:

                break;
            case CouchBase:

                //i = new CouchBaseManger(context);

               i = CouchBaseManger.getInstance();
                i.setContext(context);


              break;
            default:
                break;

        }

        return i;
    }





}
