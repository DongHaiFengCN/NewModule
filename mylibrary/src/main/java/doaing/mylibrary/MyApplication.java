package doaing;


import android.app.Application;
import android.os.Handler;
import android.widget.Toast;

import com.couchbase.lite.BasicAuthenticator;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Endpoint;
import com.couchbase.lite.Replicator;
import com.couchbase.lite.ReplicatorChange;
import com.couchbase.lite.ReplicatorChangeListener;
import com.couchbase.lite.ReplicatorConfiguration;
import com.couchbase.lite.URLEndpoint;
import com.couchbase.lite.internal.support.Log;

import java.net.URI;
import java.net.URISyntaxException;




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

public class MyApplication extends Application implements ReplicatorChangeListener {
    private static final String TAG = Application.class.getSimpleName();
    private final static boolean SYNC_ENABLED = false;

    public String getCompany_ID() {
        return Company_ID;
    }

    private String Company_ID = "gysz";
    private final static String DATABASE_NAME = "GYSZDB";
    private final static String SYNCGATEWAY_URL = "ws://123.207.174.171:4984/kitchen/";

    private Database database = null;
    private Replicator replicator;
    private String username = DATABASE_NAME;

    @Override
    public void onCreate() {
        super.onCreate();

        openDatabase(DATABASE_NAME);
        startReplication("dong", "123456");

    }

    @Override
    public void onTerminate() {
        closeDatabase();
        super.onTerminate();
    }

    public Database getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }



    // -------------------------
    // Database operation
    // -------------------------

    private void openDatabase(String dbname) {
        DatabaseConfiguration config = new DatabaseConfiguration(getApplicationContext());
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
                // TODO: error handling
            }
        }
    }

    // -------------------------
    // Replicator operation
    // -------------------------
    private void startReplication(String username, String password) {
        if (!SYNC_ENABLED) {
            return;
        }

        URI uri;
        try {
            uri = new URI(SYNCGATEWAY_URL);
        } catch (URISyntaxException e) {
            Log.e(TAG, "Failed parse URI: %s", e, SYNCGATEWAY_URL);
            return;
        }

        Endpoint endpoint = new URLEndpoint(uri);
        ReplicatorConfiguration config = new ReplicatorConfiguration(database, endpoint)
                .setReplicatorType(ReplicatorConfiguration.ReplicatorType.PUSH_AND_PULL)
                .setContinuous(true);

        // authentication
        if (username != null && password != null){
            config.setAuthenticator(new BasicAuthenticator(username, password));
        }


        replicator = new Replicator(config);
        replicator.addChangeListener(this);
        replicator.start();
    }

    private void stopReplication() {
        if (!SYNC_ENABLED){
            return;
        }

        replicator.stop();
    }

    private void runOnUiThread(Runnable runnable) {
        new Handler(getApplicationContext().getMainLooper()).post(runnable);
    }

    // --------------------------------------------------
    // ReplicatorChangeListener implementation
    // --------------------------------------------------
    @Override
    public void changed(ReplicatorChange change) {
        Log.i(TAG, "[Todo] Replicator: status -> %s", change.getStatus());
        if (change.getStatus().getError() != null && change.getStatus().getError().getCode() == 401) {
            Toast.makeText(getApplicationContext(), "Authentication Error: Your username or password is not correct.", Toast.LENGTH_LONG).show();
        }
    }

}
