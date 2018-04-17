
package doaing.dishesmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Array;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDocument;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

import doaing.dishesmanager.adapter.PackageDishesKindAdapter;
import doaing.dishesmanager.widget.DishesKindListView;
import doaing.mylibrary.MyApplication;
import tools.CDBHelper;
import tools.MyBigDecimal;
import tools.MyLog;
import tools.ToolUtil;
import view.BaseToobarActivity;

public class PackageAddActivity extends BaseToobarActivity {

    private List<Document> dishesList = new ArrayList<>();

    private Map<Integer, boolean[]> listMap = new HashMap<>();
    private MutableDocument secondDocment;
    private DishesAdapter dishesAdapter;
    @BindView(R2.id.disheskind_lv)
    ListView disheskindLv;
    @BindView(R2.id.dishes_lv)
    ListView dishesLv;
    @BindView(R2.id.submit_bt)
    Button submitBt;
    private Database database;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    float sum;
    String firstId;
    private boolean discount = false;


    private EditText packagePriceEt;
    private PackageDishesKindAdapter packageDishesKindAdapter;

    @Override
    protected int setMyContentView()
    {

        return R.layout.activity_package_add;
    }

    @Override
    public void initData(final Intent intent) {
        setToolbarName("编辑套餐");

        packageDishesKindAdapter = new PackageDishesKindAdapter(getApplicationContext(),CDBHelper.getDatabase());
        disheskindLv.setAdapter(packageDishesKindAdapter);

        packagePriceEt = findViewById(R.id.packageprice_et);

        packagePriceEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == 0 && submitBt.isEnabled()) {

                    submitBt.setEnabled(false);
                    submitBt.setTextColor(getResources().getColor(R.color.md_grey_300));

                } else if (s.length() > 0 && !submitBt.isEnabled()) {

                    submitBt.setEnabled(true);
                    submitBt.setTextColor(getResources().getColor(R.color.md_white_1000));

                }
            }
        });

        firstId = intent.getExtras().get("id").toString();

        if (packageDishesKindAdapter.getCount() == 0) {

            Toast.makeText(this, "请添加菜品", Toast.LENGTH_SHORT).show();
            finish();
            return;

        }
        database = CDBHelper.getDatabase();

        Document document = database.getDocument(firstId);


        if(document == null){

            secondDocment = new MutableDocument();
            secondDocment.setArray("dishesIds", new MutableArray());

        }else {
            secondDocment = document.toMutable();
        }



        dishesAdapter = new DishesAdapter();
        dishesLv.setAdapter(dishesAdapter);

        disheskindLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                packageDishesKindAdapter.changeSelected(position);


                if (!dishesList.isEmpty()) {
                    dishesList.clear();
                }
                dishesList = CDBHelper.getDocmentsByWhere(
                        Expression.property("className").equalTo(Expression.string("Dishes"))
                                .and(Expression.property("kindId").equalTo(Expression.string((String) packageDishesKindAdapter.getItem(position))))
                        ,null);
                //初始化菜品选择map
                if (listMap.get(position) == null) {

                    listMap.put(position, new boolean[dishesList.size()]);
                }
                dishesAdapter.setDisheKindPosition(position);
                dishesAdapter.notifyDataSetChanged();

            }
        });

        final Spinner spinner = findViewById(R.id.dishes_discount_sp);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter(PackageAddActivity.this, android.R.layout.simple_list_item_1);
        arrayAdapter.add("价格");
        arrayAdapter.add("折扣");
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    discount = false;
                    packagePriceEt.setHint("输入总价 例：80为80元");

                } else if (position == 1) {
                    discount = true;
                    packagePriceEt.setHint("输入总价 例：80为八折");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //默认选中第一项
        disheskindLv.performItemClick(disheskindLv.getChildAt(0), 0, disheskindLv
                .getItemIdAtPosition(0));

        submitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (sum == 0f) {
                    Toast.makeText(PackageAddActivity.this, "未选择菜品", Toast.LENGTH_LONG).show();
                    return;
                }

                if (discount) {

                    secondDocment.setFloat("price", Float.valueOf(MyBigDecimal.mul(String.valueOf(sum), MyBigDecimal.div(packagePriceEt.getText().toString(),String.valueOf(100),2), 2)));

                } else {
                    secondDocment.setFloat("price", Float.valueOf(packagePriceEt.getText().toString()));
                }

//                try {
//
//                    Log.e("DOAING", "保存的价格 " + secondDocment.getFloat("price"));
//
//                    database.save(secondDocment);
//
//                } catch (CouchbaseLiteException e) {
//
//                    e.printStackTrace();
//                }

                //EventBus.getDefault().postSticky(secondDocment.getId());
                if (discount) {

                    EventBus.getDefault().postSticky(Float.valueOf(MyBigDecimal.mul(String.valueOf(sum), MyBigDecimal.div(packagePriceEt.getText().toString(),String.valueOf(100),2), 2)));

                } else {
                    EventBus.getDefault().postSticky(Float.valueOf(packagePriceEt.getText().toString()));
                }

                EventBus.getDefault().postSticky(secondDocment.getArray("dishesIds"));
                finish();

            }
        });



    }

    private float getSum() {

        float sum = 0;
        int size = packageDishesKindAdapter.getCount();

        if (secondDocment.getArray("dishesIds").count() > 0) {

            secondDocment.setArray("dishesIds", new MutableArray());
        }

        for (int i = 0; i < size; i++) {

            boolean[] booleans = listMap.get(i);

            if (booleans == null) {

                continue;

            }

            int length = booleans.length;

            for (int j = 0; j < length; j++) {


                if (booleans[j]) {
                    Document dishe = dishesList.get(j);
                    sum += dishe.getFloat("price");
                    secondDocment.getArray("dishesIds").addString(dishe.getId());
                }
            }
        }
        return sum;
    }


    @Override
    protected Toolbar setToolBarInfo() {
        return toolbar;
    }

    public class DishesAdapter extends BaseAdapter {

        public void setDisheKindPosition(int disheKindPosition) {
            this.disheKindPosition = disheKindPosition;
        }

        int disheKindPosition;

        @Override
        public int getCount() {
            return dishesList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ListItemView listItemView;

            if (convertView == null) {

                listItemView = new ListItemView();
                convertView = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.dishes_select_listview_item, null);
                listItemView.disheNameTv = convertView.findViewById(R.id.dishename_tv);
                listItemView.dishePriceTv = convertView.findViewById(R.id.dishesprice_tv);
                listItemView.dishesCk = convertView.findViewById(R.id.dishe_ck);
                convertView.setTag(listItemView);


            } else {
                listItemView = (ListItemView) convertView.getTag();
            }
            Document document = dishesList.get(position);

            if (document != null) {

                listItemView.disheNameTv.setText(document.getString("name"));
                listItemView.dishePriceTv.setText("¥" + document.getFloat("price"));
                listItemView.dishesCk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        listMap.get(disheKindPosition)[position] = isChecked;

                        sum = getSum();

                        getSupportActionBar().setTitle("编辑套餐  当前总价：" + sum);

                    }
                });

                listItemView.dishesCk.setChecked(listMap.get(disheKindPosition)[position]);

            }


            return convertView;
        }

        public class ListItemView {

            TextView disheNameTv;
            TextView dishePriceTv;
            CheckBox dishesCk;

        }
    }

}

