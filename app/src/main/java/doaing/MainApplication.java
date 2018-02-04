package doaing;

import android.annotation.SuppressLint;
import android.app.Application;

import com.mob.MobApplication;
import com.mob.MobSDK;

import doaing.mylibrary.MyApplication;


/**
 * 项目名称：NewModule
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2018/2/3 17:34
 * 修改人：donghaifeng
 * 修改时间：2018/2/3 17:34
 * 修改备注：
 * @author donghaifeng
 */

public class MainApplication extends MyApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        MobSDK.init(this);
    }
}
