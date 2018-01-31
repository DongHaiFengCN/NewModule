package doaing.dishesmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;

import butterknife.BindView;
import module.MyApplication;
import tools.ToolUtil;
import view.BaseToobarActivity;

public class PackageAddActivity extends BaseToobarActivity {
    private Database database;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected int setMyContentView() {
        return R.layout.activity_package_add;
    }

    @Override
    public void initData(Intent intent) {

        //Log.e("DOAING",intent.getExtras().get("id").toString());
        database = ((MyApplication) getApplicationContext()).getDatabase();
        Document document = database.getDocument(intent.getExtras().get("id").toString());

       /* document.getArray("dishesListId").remove(0);
        try {
            database.save(document);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }*/
 /*       Document document1 = new Document("DishesC." + ToolUtil.getUUID());
        document1.setString("channelId", ((MyApplication) getApplication()).getCompany_ID());
        document1.setString("className", "DishesC");
        document1.setString("dishesName", "测试二级套餐");
        document1.setString("dishesKindId",document.getId());
        try {
            database.save(document1);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        document.getArray("dishesListId").addString(document1.getId());
        try {
            database.save(document);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    protected Toolbar setToolBarInfo() {
        return toolbar;
    }
}
