package doaing.mylibrary;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.android.arouter.launcher.ARouter;
import com.couchbase.lite.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import bean.kitchenmanage.dishes.DishesKind;
import bean.kitchenmanage.table.Table;
import bean.kitchenmanage.user.Employee;
import tools.PriorityExecutor;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/8 13:26
 * @author ：donghaifeng
 * 修改时间：2017/9/8 13:26
 * 修改备注：
 */

public class MyApplication extends Application  {

    private static final String TAG = Application.class.getSimpleName();

    private final static boolean SYNC_ENABLED = true;

    public static boolean WX_RECEIVE_FLAG = false;

    public Map<String, List<Document>> getDishesObjectCollection() {
        return dishesObjectCollection;
    }

    public void setDishesObjectCollection(Map<String, List<Document>> dishesObjectCollection) {

        this.dishesObjectCollection = dishesObjectCollection;

    }

    private Map<String, List<Document>> dishesObjectCollection = new HashMap<>();

    public List<DishesKind> getDishesKindCList() {
        return dishesKindCList;
    }

    public void setDishesKindCList(List<DishesKind> dishesKindCList) {
        this.dishesKindCList = dishesKindCList;
    }
    List<DishesKind> dishesKindCList;
    private String Company_ID = "gysz";
    private Table table_sel_obj;
    private Employee employee;
    public ExecutorService mExecutor;

    @Override
    public void onCreate() {
        super.onCreate();
        /*
        产品进入生产环节，这块还要加上
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
        strategy.setAppChannel("开发部");
        Bugly.init(getApplicationContext(), "c11c0d8e58", true, strategy);
        CrashReport.setUserId("1002");
        */


        mExecutor =  new PriorityExecutor(5, false);
        //初始化阿里路由
        ARouter.openDebug();
        // 尽可能早，推荐在Application中初始化
        ARouter.init(this);

    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @Override
    public void onTerminate() {

        if (mExecutor != null && !mExecutor.isShutdown()) {
            mExecutor.shutdown();
        }
        super.onTerminate();
    }
    public String getCompany_ID() {
        return Company_ID;
    }
    public void setCompany_ID(String company_ID) {
        Company_ID = company_ID;
    }
    public Table getTable_sel_obj() {
        return table_sel_obj;
    }
    public void setTable_sel_obj(Table table_sel_obj) {
        this.table_sel_obj = table_sel_obj;
    }
}
