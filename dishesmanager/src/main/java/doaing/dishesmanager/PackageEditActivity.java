package doaing.dishesmanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Array;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDocument;
import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bean.kitchenmanage.dishes.DishesC;
import butterknife.BindView;
import doaing.dishesmanager.view.MySwipeListLayout;

import rx.functions.Action1;
import tools.CDBHelper;
import tools.MyBigDecimal;
import view.BaseToobarActivity;

/**
 * @author donghaifeng
 */

public class PackageEditActivity extends BaseToobarActivity {

    private List<Document> list = new ArrayList<>();
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.dishes_lv)
    ListView dishesLv;
    @BindView(R2.id.revise_info_bt)
    Button submit;
    @BindView(R2.id.revise_price_et)
    EditText revisePriceEt;

    private Document oneLevel;
    private Document secondLevel;
    private Database database;
    private ListAdapter listAdapter;
    private Spinner spinner;
    private boolean discount = false;
    private float sum = 0f;

    @Override
    protected int setMyContentView() {
        return R.layout.activity_package_edit;
    }

    @Override
    public void initData(Intent intent) {

        spinner = findViewById(R.id.dishes_discount_sp);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        arrayAdapter.add("价格");
        arrayAdapter.add("折扣");
        spinner.setAdapter(arrayAdapter);


        database = CDBHelper.getDatabase();
        oneLevel = database.getDocument(String.valueOf(intent.getExtras().get("kindId")));
        secondLevel = database.getDocument(String.valueOf(intent.getExtras().get("disheId")));

        setToolbarName("当前套餐总价 " + secondLevel.getFloat("price"));

        listAdapter = new ListAdapter();

        dishesLv.setAdapter(listAdapter);
        Array array = secondLevel.getArray("dishesIdList");

        int length = array.count();
        Document temporary;
        for (int i = 0; i < length; i++) {
            temporary = database.getDocument(array.getString(i));
            if (temporary.getString("dishesName") != null) {
                list.add(temporary);
                sum += temporary.getFloat("price");
            }
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    discount = false;
                    revisePriceEt.setHint("原价" + sum + " 例：80为80元");

                } else if (position == 1) {
                    discount = true;
                    revisePriceEt.setHint("原价"+ sum + " 例：80为八折");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listAdapter.notifyDataSetChanged();

        //提交编辑套餐编辑后的数据
        RxView.clicks(submit).throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                        MutableDocument secondLevelMuDoc = secondLevel.toMutable();

                        String price = revisePriceEt.getText().toString();

                        if (!"".equals(price)) {

                            if(discount){

                                float discount = MyBigDecimal.div(Float.valueOf(price), 100f, 2);
                                secondLevelMuDoc.setFloat("price", MyBigDecimal.mul(sum, discount, 2));
                            }else {

                                secondLevelMuDoc.setFloat("price", Float.valueOf(price));
                            }

                        }else {
                            revisePriceEt.setError("请输入修改价格或折扣");
                            return;
                        }
                        try {
                            MutableArray array = new MutableArray();
                            int size = list.size();
                            for (int i = 0; i < size; i++) {

                                array.addString(list.get(i).getId());
                            }
                            secondLevelMuDoc.setArray("dishesIdList", array);
                            database.save(secondLevelMuDoc);
                            finish();
                            Toast.makeText(PackageEditActivity.this,"修改成功！",Toast.LENGTH_SHORT).show();

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

                    MutableDocument mutableDocument = oneLevel.toMutable();

                    MutableArray array = mutableDocument.getArray("dishesListId");
                    int length = array.count();

                    for (int i = 0; i < length; i++) {

                        if (secondLevel.getId().equals(array.getString(i))) {

                            array.remove(i);
                            break;
                        }

                    }

                    try {
                        database.delete(secondLevel);
                        database.save(mutableDocument);
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

        @SuppressLint({"SetTextI18n", "InflateParams"})
        @Override
        public View getView(final int arg0, View view, ViewGroup arg2) {
            if (view == null) {
                view = LayoutInflater.from(PackageEditActivity.this).inflate(
                        R.layout.package_edit_list_item, null);
            }
            final TextView tvName = view.findViewById(R.id.tv_name);
            tvName.setText(list.get(arg0).getString("dishesName"));
            final TextView tvPrice = view.findViewById(R.id.tv_price);
            tvPrice.setText("¥" + list.get(arg0).getFloat("price"));

            final MySwipeListLayout sllMain = view.findViewById(R.id.sll_main);

            TextView tvDelete = view.findViewById(R.id.tv_delete);

            tvDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    new AlertDialog.Builder(PackageEditActivity.this).setTitle("删除套餐菜品")
                            .setMessage(tvName.getText().toString())
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sllMain.setStatus(MySwipeListLayout.Status.Close, true);
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

