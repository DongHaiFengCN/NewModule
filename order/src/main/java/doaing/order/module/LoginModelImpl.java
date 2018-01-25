package doaing.order.module;

import android.content.Context;
import android.content.SharedPreferences;

import doaing.order.untils.NetworkStatusUtil;


/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/8 12:19
 * 修改人：donghaifeng
 * 修改时间：2017/9/8 12:19
 * 修改备注：
 */

public class LoginModelImpl implements ILoginModel{

    String info[];

    public LoginModelImpl()
    {

    }
    /**
     * 网络访问检测
     *
     * @return
     */
    @Override
    public boolean Networkconnectionfailed(Context context) {


        return NetworkStatusUtil.isNetworkAvailable(context);
    }

    @Override
    public boolean isExit( Context context) {

            //判断数据库中是否有信息

         IDBManager idbManager = DBFactory.get(DatabaseSource.CouchBase,context);//返回当前数据库

        return idbManager.isLogin(info[0],info[1]);
    }

    @Override
    public boolean isEmpty(String[] info) {

        this.info = info;

        return "".equals(info[0]) || "".equals(info[1]);
    }

    @Override
    public void saveStatus(SharedPreferences mSharedPreferences) {


        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putString("name",info[0]);
        editor.putString("password",info[1]);
        editor.commit();
    }




}
