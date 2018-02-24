package smartkitchen.com.login.globle;

import android.app.Application;
import android.util.Log;

import com.alibaba.android.arouter.BuildConfig;
import com.alibaba.android.arouter.launcher.ARouter;
import com.mob.MobSDK;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;




public class MyApplication extends Application
{
    private static final String TAG = Application.class.getSimpleName();
    public ExecutorService mExecutor;
    @Override
    public void onCreate()
    {
        super.onCreate();
        mExecutor = Executors.newCachedThreadPool();
        MobSDK.init(this);
//        if (BuildConfig.DEBUG) {
//            ARouter.openLog();     // 打印日志
//            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
//        }
        ARouter.openDebug();
        ARouter.init(this);


    }
    @Override
    public void onTerminate() {

        Log.e(TAG,"application onTerminate");
        if (mExecutor != null && !mExecutor.isShutdown()) {
            mExecutor.shutdown();
        }

        super.onTerminate();
    }








}
