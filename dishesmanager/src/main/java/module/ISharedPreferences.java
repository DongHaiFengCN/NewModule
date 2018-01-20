package module;

import android.content.SharedPreferences;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/8 13:41
 * 修改人：donghaifeng
 * 修改时间：2017/9/8 13:41
 * 修改备注：
 */

public interface ISharedPreferences {

    //SharedPreferences
    SharedPreferences getSharePreferences();

    boolean cancleSharePreferences();

}
