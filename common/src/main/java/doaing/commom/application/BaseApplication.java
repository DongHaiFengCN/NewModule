package doaing.commom.application;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;

/**
 * 项目名称：new
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2018/1/17 11:55
 * 修改人：donghaifeng
 * 修改时间：2018/1/17 11:55
 * 修改备注：
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ARouter.init(this);
    }
}
