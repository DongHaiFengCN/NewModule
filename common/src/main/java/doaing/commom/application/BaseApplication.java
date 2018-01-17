package doaing.commom.application;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * 项目名称：new
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2018/1/17 11:55
 * 修改人：donghaifeng
 * 修改时间：2018/1/17 11:55
 * 修改备注：
 */

/**
 * @Linke CrashReport
 * 第三个参数为SDK调试模式开关，调试模式的行为特性如下：
 * 输出详细的Bugly SDK的Log；
 * 每一条Crash都会被立即上报；
 * 自定义日志将会在Logcat中输出。
 * 建议在测试阶段建议设置成true，发布时设置为false。
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ARouter.init(this);
        CrashReport.initCrashReport(getApplicationContext(), "8bd719c45f", true);
    }


    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }
}
