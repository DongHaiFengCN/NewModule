package tools;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = CrashHandler.class.getSimpleName();

    private static CrashHandler instance; // 单例模式

    private Context context; // 程序Context对象
    private Thread.UncaughtExceptionHandler defalutHandler; // 系统默认的UncaughtException处理类
    private DateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd_HH-mm-ss.SSS", Locale.CHINA);
    // 静态内部类
    private static class SingleClass {
        private static CrashHandler instance;
        static {
            instance = new CrashHandler();
        }
    }

    /**
     * 异常处理初始化
     *
     * @param context
     */
    public void init(Context context) {
        this.context = context;
        // 获取系统默认的UncaughtException处理器
        defalutHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }
    public static CrashHandler getInstance() {

        return SingleClass.instance;
    }
    private CrashHandler() {

    }
    @Override
    public void uncaughtException(Thread t, Throwable e) {

        Log.e(TAG,Log.getStackTraceString(e));
        // 退出程序
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }}

