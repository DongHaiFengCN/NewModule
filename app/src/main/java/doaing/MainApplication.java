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
import com.mob.MobSDK;

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

public class MainApplication extends doaing.MyApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        MobSDK.init(this);
    }
}
