package doaing.mylibrary;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.couchbase.lite.Document;
import com.couchbase.lite.MutableDocument;
import com.mob.MobApplication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import bean.kitchenmanage.dish.DishesKind;
import bean.kitchenmanage.table.Table;
import bean.kitchenmanage.user.Employee;
import tools.CDBHelper;
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

public class MyApplication extends MobApplication {

    private static final String TAG = Application.class.getSimpleName();

    private final static boolean SYNC_ENABLED = true;

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
    private MutableDocument table_sel_obj;
    private Document employee;
    public ExecutorService mExecutor;
    public boolean isAdmin;
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

    public Document getEmployee() {
        return employee;
    }

    public void setEmployee(Document employee) {
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
    public MutableDocument getTable_sel_obj() {
        return table_sel_obj;
    }
    public void setTable_sel_obj(MutableDocument table_sel_obj) {
        this.table_sel_obj = table_sel_obj;
    }

    public void setAdmin(boolean admin, String id) {
        isAdmin = admin;
/*        if (isAdmin) {
            //1判断有没有管理员

            MutableDocument mutableDocument = null;
            Document document = null;
            Database database = CDBHelper.getDatabase();
            Query query = QueryBuilder.select(SelectResult.expression(Meta.id))
                    .from(DataSource.database(CDBHelper.getDatabase()))
                    .where(Expression.property("className").equalTo(Expression.string
                            ("Employee"))
                            .and(Expression.property("isAdmin")
                                    .equalTo(Expression.booleanValue(true))));

            try {

                ResultSet resultSet = query.execute();
                Result row;

                while ((row = resultSet.next()) != null) {

                    document = database.getDocument(row.getString(0));

                //    Log.e("DOAING", "数据库中的管理员姓名" + document.getString("name"));
                 //   Log.e("DOAING", "数据库中的管理员标志" + document.getBoolean("isAdmin"));

                }

            } catch (CouchbaseLiteException e) {

                e.printStackTrace();
            }

            //2 没有就直接创建一个



            if (document == null) {

                mutableDocument = new MutableDocument("Employee." + ToolUtil
                        .getUUID());
                mutableDocument.setString("channelId", getCompany_ID());
                mutableDocument.setString("dataType ", "basic");
                mutableDocument.setString("className", "Employee");
                mutableDocument.setBoolean("isAdmin", true);
                mutableDocument.setString("name", "管理员");
                try {
                    database.save(mutableDocument);
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }

               // Log.e("DOAING", "没有创建一个管理员");

            }else {

                mutableDocument = document.toMutable();
            }*/

        //3 有就直接赋值

        employee = CDBHelper.getDocByID(id);

        // Log.e("DOAING", "赋值姓名：" + usersC.getString("name"));


        // }
    }


}
