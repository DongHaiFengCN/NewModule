package module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.couchbase.lite.BasicAuthenticator;
import com.couchbase.lite.Conflict;
import com.couchbase.lite.ConflictResolver;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.Log;
import com.couchbase.lite.ReadOnlyDocument;
import com.couchbase.lite.Replicator;
import com.couchbase.lite.ReplicatorChange;
import com.couchbase.lite.ReplicatorChangeListener;
import com.couchbase.lite.ReplicatorConfiguration;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;

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

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/8 13:26
 * 修改人：donghaifeng
 * 修改时间：2017/9/8 13:26
 * 修改备注：
 */

public class MyApplication extends Application implements ISharedPreferences, ReplicatorChangeListener {

    private static final String TAG = Application.class.getSimpleName();

    private final static boolean SYNC_ENABLED = true;

    public Map<String, List<Document>> getDishesObjectCollection() {
        return dishesObjectCollection;
    }

    public void setDishesObjectCollection(Map<String, List<Document>> dishesObjectCollection) {

        this.dishesObjectCollection = dishesObjectCollection;

    }

    private Map<String, List<Document>> dishesObjectCollection = new HashMap<>();

 /*   public List<DishesKindC> getDishesKindCList() {
        return dishesKindCList;
    }

    public void setDishesKindCList(List<DishesKindC> dishesKindCList) {
        this.dishesKindCList = dishesKindCList;
    }

    List<DishesKindC> dishesKindCList;*/

//    private String Company_ID="gysz";
//    private final static String DATABASE_NAME = "gyszdb";
    //private final static String SYNCGATEWAY_URL = "blip://123.207.174.171:4984/kitchendb/";

    private String Company_ID = "gysz";
    //private final static String DATABASE_NAME = "gyszdbD";
    private final static String DATABASE_NAME = "GYSZDB";

    private final static String SYNCGATEWAY_URL = "blip://192.168.2.166:4984/kitchendb/";


    private Database database = null;
    private Replicator replicator;


/*    private TableC table_sel_obj;

    public UsersC getUsersC() {
        return usersC;
    }

    public void setUsersC(UsersC usersC) {
        this.usersC = usersC;
    }

    private UsersC usersC;*/

    public ExecutorService mExecutor;


    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
        strategy.setAppChannel("开发部");
        Bugly.init(getApplicationContext(), "c11c0d8e58", true, strategy);
        CrashReport.setUserId("1002");
        startSession(DATABASE_NAME);
        mExecutor = Executors.newCachedThreadPool();
    }


    @Override
    public void onTerminate() {

        if (mExecutor != null && !mExecutor.isShutdown()) {
            mExecutor.shutdown();
        }
        closeDatabase();
        super.onTerminate();
    }

    private void startSession(String dbName) {
        openDatabase(dbName);
        startReplication(getCompany_ID(), "123456");
    }

    // -------------------------
    // Database operation
    // -------------------------

    private void openDatabase(String dbname) {

        DatabaseConfiguration config = new DatabaseConfiguration(getApplicationContext());
       // File folder = new File(String.format("%s/SmartKitchenPad", Environment.getExternalStorageDirectory()));
        //config.setDirectory(folder);
        config.setConflictResolver(getConflictResolver());
        try {
            database = new Database(dbname, config);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Failed to create Database instance: %s - %s", e, dbname, config);
            // TODO: error handling
        }
    }

    private void closeDatabase() {
        if (database != null) {
            try {
                database.close();
            } catch (CouchbaseLiteException e) {
                Log.e(TAG, "Failed to close Database", e);
            }
        }

    }

    public Database getDatabase() {
        return database;
    }

    private ConflictResolver getConflictResolver() {
        /**
         * Example: Conflict resolver that merges Mine and Their document.
         */
        return new ConflictResolver() {
            @Override
            public ReadOnlyDocument resolve(Conflict conflict) {
                ReadOnlyDocument mine = conflict.getMine();
                ReadOnlyDocument theirs = conflict.getTheirs();

                Document resolved = new Document();
                Set<String> changed = new HashSet<>();

                // copy all data from theirs document
                for (String key : theirs) {
                    Log.e("ConflictResolvertheir", "key=" + key + "value=" + theirs.getObject(key));
                    resolved.setObject(key, theirs.getObject(key));
                    changed.add(key);
                }

                // copy all data from mine which are not in mine document
                for (String key : mine) {
                    Log.e("ConflictResolvermine", "key=" + key + "value=" + mine.getObject(key));
                    if (!changed.contains(key))
                        resolved.setObject(key, mine.getObject(key));
                }

                // Log.e(TAG, "ConflictResolver.resolve() resolved -> %s", resolved.toMap());

                return resolved;
            }
        };
    }

    // -------------------------
    // Replicator operation
    // -------------------------
    private void startReplication(String username, String password) {
        if (!SYNC_ENABLED) return;

        URI uri;
        try {
            uri = new URI(SYNCGATEWAY_URL);
        } catch (URISyntaxException e) {
            Log.e(TAG, "Failed parse URI: %s", e, SYNCGATEWAY_URL);
            return;
        }

        ReplicatorConfiguration config = new ReplicatorConfiguration(database, uri);
        List<String> channels = new ArrayList<>();

//        MyLog.d("companyid="+getCompany_ID());
//        channels.add(getCompany_ID());
//        config.setChannels(channels);

        config.setReplicatorType(ReplicatorConfiguration.ReplicatorType.PUSH_AND_PULL);
        config.setContinuous(true);

        // authentication
         Log.e("----->","username="+username+"____psw="+password);
        if (username != null && password != null)
            config.setAuthenticator(new BasicAuthenticator(username, password));

        replicator = new Replicator(config);
        replicator.addChangeListener(this);
        replicator.start();
    }

    private void stopReplication() {
        if (!SYNC_ENABLED) return;

        replicator.stop();
    }

    @Override
    public SharedPreferences getSharePreferences() {

        return getSharedPreferences("loginUser", Context.MODE_PRIVATE);
    }

    @Override
    public boolean cancleSharePreferences() {

        return getSharePreferences().edit().clear().commit();
    }

    // --------------------------------------------------
    // ReplicatorChangeListener implementation
    // --------------------------------------------------
//    @Override
//    public void changed(Replicator replicator, Replicator.Status status, CouchbaseLiteException error)
//    {
//       // Log.e(TAG, "[Todo] Replicator: status -> %s, error -> %s", status, error);
//
//    }
    @Override
    public void changed(ReplicatorChange change) {
        // Log.e(TAG, "[Todo] Replicator: status -> %s, error -> %s", status, error);

    }

    public String getCompany_ID() {
        return Company_ID;
    }

    public void setCompany_ID(String company_ID) {
        Company_ID = company_ID;
    }
/*
    public TableC getTable_sel_obj() {
        return table_sel_obj;
    }

    public void setTable_sel_obj(TableC table_sel_obj) {
        this.table_sel_obj = table_sel_obj;
    }*/
}
