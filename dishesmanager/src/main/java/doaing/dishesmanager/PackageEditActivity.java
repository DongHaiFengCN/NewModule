package doaing.dishesmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.couchbase.lite.Array;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import doaing.dishesmanager.view.MySwipeListLayout;
import module.MyApplication;
import rx.functions.Action1;
import view.BaseToobarActivity;

/**
 * @author donghaifeng
 */
public class PackageEditActivity extends BaseToobarActivity {

    private List<Document> list = new ArrayList<>();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.dishes_lv)
    ListView dishesLv;
    @BindView(R.id.revise_info_bt)
    Button submit;
    @BindView(R.id.revise_price_et)
    EditText revisePriceEt;

    Document oneLevel;
    Document secondLevel;
    Database database;
    ListAdapter listAdapter;

    @Override
    protected int setMyContentView() {
        return R.layout.activity_package_edit;
    }

    @Override
    public void initData(Intent intent) {
        database = ((MyApplication) getApplicationContext()).getDatabase();
        oneLevel = database.getDocument(intent.getExtras().get("kindId").toString
                ());
        secondLevel = database.getDocument(intent.getExtras().get("disheId").toString());

        setToolbarName(secondLevel.getString("dishesName") + "  总价" + secondLevel.getFloat("price"));

        listAdapter = new ListAdapter();

        dishesLv.setAdapter(listAdapter);
        Array array = secondLevel.getArray("dishesListId");

        int Length = array.count();

        Document temporary = null;

        for (int i = 0; i < Length; i++) {


            temporary = database.getDocument(array.getString(i));
            list.add(temporary);

        }

        listAdapter.notifyDataSetChanged();

        RxView.clicks(submit).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {

                String price = revisePriceEt.getText().toString();

                if (!"".equals(price)) {

                    secondLevel.setFloat("", Float.valueOf(price));
                }
                try {

                    database.save(secondLevel);
                    finish();

                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }

            }
        });

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


                    Array array = oneLevel.getArray("dishesListId");
                    int length = array.count();

                    for (int i = 0; i < length; i++) {

                        if (secondLevel.getId().equals(array.getString(i))) {

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

    class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int arg0) {
            return list.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(final int arg0, View view, ViewGroup arg2) {
            if (view == null) {
                view = LayoutInflater.from(PackageEditActivity.this).inflate(
                        R.layout.package_edit_list_item, null);
            }
            final TextView tv_name = view.findViewById(R.id.tv_name);
            tv_name.setText(list.get(arg0).getString("dishesName"));
            final TextView tv_price = view.findViewById(R.id.tv_price);
            tv_price.setText("¥" + list.get(arg0).getFloat("price"));

            final MySwipeListLayout sll_main = view.findViewById(R.id.sll_main);

            TextView tv_delete = view.findViewById(R.id.tv_delete);

            tv_delete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    new AlertDialog.Builder(PackageEditActivity.this).setTitle("删除套餐菜品")
                            .setMessage(tv_name.getText().toString())
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    sll_main.setStatus(MySwipeListLayout.Status.Close, true);
                                    Array array = secondLevel.getArray("dishesListId");
                                    int Length = array.count();

                                    for (int i = 0; i < Length; i++) {

                                        if (list.get(arg0).getId().equals(array.getString(i))) {

                                            array.remove(i);
                                        }

                                    }


                                    list.remove(arg0);
                                    notifyDataSetChanged();
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();

                }
            });
            return view;
        }

    }
}
