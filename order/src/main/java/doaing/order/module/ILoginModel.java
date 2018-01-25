package doaing.order.module;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/8 12:19
 * 修改人：donghaifeng
 * 修改时间：2017/9/8 12:19
 * 修改备注：
 */

public interface ILoginModel {

    // 网络连接失败
    boolean  Networkconnectionfailed(Context context);

    //验证失败
    boolean isExit(Context context);

    //验证填入的数据是否有空值
    boolean isEmpty(String[] info);

    //保存登陆信息
    void saveStatus(SharedPreferences mSharedPreferences);






}
