package doaing.mylibrary;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.alibaba.android.arouter.launcher.ARouter;
import com.couchbase.lite.BasicAuthenticator;
import com.couchbase.lite.Conflict;
import com.couchbase.lite.ConflictResolver;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.Replicator;
import com.couchbase.lite.ReplicatorChange;
import com.couchbase.lite.ReplicatorChangeListener;
import com.couchbase.lite.ReplicatorConfiguration;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import bean.kitchenmanage.dishes.DishesKindC;
import bean.kitchenmanage.table.TableC;
import bean.kitchenmanage.user.UsersC;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/8 13:26
 * 修改人：donghaifeng
 * 修改时间：2017/9/8 13:26
 * 修改备注：
 */

public class MyApplication extends Application implements ISharedPreferences {

    private static final String TAG = Application.class.getSimpleName();

    private final static boolean SYNC_ENABLED = true;

    public Map<String, List<Document>> getDishesObjectCollection() {
        return dishesObjectCollection;
    }

    public void setDishesObjectCollection(Map<String, List<Document>> dishesObjectCollection) {

        this.dishesObjectCollection = dishesObjectCollection;

    }

    private Map<String, List<Document>> dishesObjectCollection = new HashMap<>();

    public List<DishesKindC> getDishesKindCList() {
        return dishesKindCList;
    }

    public void setDishesKindCList(List<DishesKindC> dishesKindCList) {
        this.dishesKindCList = dishesKindCList;
    }

    List<DishesKindC> dishesKindCList;


    private String Company_ID = "gysz";



    private TableC table_sel_obj;

    public UsersC getUsersC() {
        return usersC;
    }

    public void setUsersC(UsersC usersC) {
        this.usersC = usersC;
    }

    private UsersC usersC;

    public ExecutorService mExecutor;

    @Override
    public void onCreate() {
        super.onCreate();
        /*CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
        strategy.setAppChannel("开发部");
        Bugly.init(getApplicationContext(), "c11c0d8e58", true, strategy);
        CrashReport.setUserId("1002");*/

        mExecutor = Executors.newCachedThreadPool();
        //初始化阿里路由
        ARouter.openDebug();
        ARouter.init(this); // 尽可能早，推荐在Application中初始化

    }


    @Override
    public void onTerminate() {

        if (mExecutor != null && !mExecutor.isShutdown()) {
            mExecutor.shutdown();
        }
        super.onTerminate();
    }


    @Override
    public SharedPreferences getSharePreferences() {

        return getSharedPreferences("loginUser", Context.MODE_PRIVATE);
    }

    @Override
    public boolean cancleSharePreferences() {

        return getSharePreferences().edit().clear().commit();
    }




    public String getCompany_ID() {
        return Company_ID;
    }

    public void setCompany_ID(String company_ID) {
        Company_ID = company_ID;
    }
    public TableC getTable_sel_obj() {
        return table_sel_obj;
    }

    public void setTable_sel_obj(TableC table_sel_obj) {
        this.table_sel_obj = table_sel_obj;
    }
}
