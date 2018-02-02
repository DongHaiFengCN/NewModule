package doaing.dishesmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.couchbase.lite.Array;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import module.MyApplication;
import view.BaseToobarActivity;

public class PackageEditActivity extends BaseToobarActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    Document oneLevel;
    Document secondLevel;
    Database database;

    @Override
    protected int setMyContentView() {
        return R.layout.activity_package_edit;
    }

    @Override
    public void initData(Intent intent) {
        database =((MyApplication)getApplicationContext()).getDatabase();
        oneLevel = database.getDocument(intent.getExtras().get("kindId").toString());
        secondLevel = database.getDocument(intent.getExtras().get("disheId").toString());


    }

    @Override
    protected Toolbar setToolBarInfo() {
        return toolbar;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toobar_delet, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == R.id.action_delet) {

            new AlertDialog.Builder(this)
                    .setMessage("确定删除当前套餐吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    Array array =  oneLevel.getArray("dishesListId");
                    int length = array.count();

                    for (int i = 0; i < length; i++) {

                        if(secondLevel.getId().equals(array.getString(i))){

                            array.remove(i);
                            break;
                        }

                    }


                    try {
                        database.delete(secondLevel);
                        database.save(oneLevel);
                        finish();
                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    }
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();


        }

        return super.onOptionsItemSelected(item);
    }

}
