
package doaing.dishesmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Array;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDocument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

import doaing.MyApplication;
import doaing.dishesmanager.widget.DishesKindListView;
import view.BaseToobarActivity;

public class PackageAddActivity extends BaseToobarActivity {

    private List<Document> dishesList = new ArrayList<>();

    private Map<Integer, boolean[]> listMap = new HashMap<>();
    private MutableDocument disheDcoument;
    private DishesAdapter dishesAdapter;
    @BindView(R2.id.disheskind_lv)
    DishesKindListView disheskindLv;
    @BindView(R2.id.dishes_lv)
    ListView dishesLv;
    @BindView(R2.id.submit_bt)
    Button submitBt;
    private Database database;
    Document packageDcoument;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;


    @Override
    protected int setMyContentView() {
        return R.layout.activity_package_add;
    }

    @Override
    public void initData(final Intent intent) {
        setToolbarName("添加二级套餐");
        if(disheskindLv.getDishesKindList().size() ==0){
           Toast.makeText(this,"没有菜品",Toast.LENGTH_SHORT).show();
            finish();
        }
        database = ((MyApplication) getApplicationContext()).getDatabase();
        packageDcoument = database.getDocument(intent.getExtras().get("id").toString());

        disheDcoument = new MutableDocument();
        disheDcoument.setArray("dishesListId", new MutableArray());



        dishesAdapter = new DishesAdapter();
        dishesLv.setAdapter(dishesAdapter);

        disheskindLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                disheskindLv.getDishesKindAdapter().changeSelected(position);

                Document dishesKind = disheskindLv.getDishesKindList().get(position);

                Array array = dishesKind.getArray("dishesListId");


                if (!dishesList.isEmpty()) {
                    dishesList.clear();
                }
                if (array == null) {

                    return;
                }
                for (int i = 0; i < array.count(); i++) {

                    dishesList.add(database.getDocument(array.getString(i)));
                }
                //初始化菜品选择map
                if (listMap.get(position) == null) {

                    listMap.put(position, new boolean[dishesList.size()]);
                }
                dishesAdapter.setDisheKindPosition(position);
                dishesAdapter.notifyDataSetChanged();

            }
        });


        //默认选中第一项
        disheskindLv.performItemClick(disheskindLv.getChildAt(0), 0, disheskindLv
                .getItemIdAtPosition(0));

        submitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                float sum = 0;
                int size = disheskindLv.getDishesKindList().size();
                for (int i = 0; i < size; i++) {

                    boolean[] booleans = listMap.get(i);
                    if (booleans == null) {
                        continue;

                    }
                    Document document = disheskindLv.getDishesKindList().get(i);
                    int length = booleans.length;
                    for (int j = 0; j < length; j++) {

                        if (booleans[j]) {
                            Document dishe = database.getDocument(document.getArray("dishesListId").getString(j));
                            sum += dishe.getFloat("price");

                            disheDcoument.getArray("dishesListId").addString(dishe.getId());


                        }
                    }
                }
                if (sum == 0f) {
                    Toast.makeText(PackageAddActivity.this, "未选择菜品", Toast.LENGTH_LONG).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(PackageAddActivity.this);
                builder.setTitle("选择套餐总价计算方式");
                final float finalSum = sum;

                builder.setPositiveButton("自定折扣", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {


                        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.package_dialog_item, null);
                        final EditText packageNameEt = view.findViewById(R.id.packagename_et);
                        final EditText packagePriceEt = view.findViewById(R.id.packageprice_et);
                        packagePriceEt.setHint("例如：80 等于打八折");
                        AlertDialog alertDialog = new AlertDialog.Builder(PackageAddActivity.this)
                                .setTitle("折扣  当前总价：" + finalSum).setView(view)
                                .setPositiveButton("确定", null)
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();
                        alertDialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if ("".equals(packageNameEt.getText().toString()) && "".equals(packagePriceEt.getText().toString())) {

                                    packageNameEt.setError("不能为空！");
                                    packagePriceEt.setError("不能为空！");

                                } else {
                                    float sum = Float.valueOf(packagePriceEt.getText().toString());

                                    disheDcoument.setString("dishesName",packageNameEt.getText().toString());

                                    //保存数据库,设置套餐价格
                                    disheDcoument.setFloat("price", finalSum * (sum / 100f));
                                    MutableDocument mutableDocument = packageDcoument.toMutable();
                                    mutableDocument.getArray("dishesListId").addString(disheDcoument.getId());
                                    try {
                                        database.save(mutableDocument);
                                        database.save(disheDcoument);
                                    } catch (CouchbaseLiteException e) {
                                        e.printStackTrace();
                                    }
                                    dialog.dismiss();
                                    PackageAddActivity.this.finish();

                                }

                            }
                        });

                    }
                }).setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton("自定总价", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.package_dialog_item, null);

                        final EditText packageNameEt = view.findViewById(R.id.packagename_et);
                        final EditText packagePriceEt = view.findViewById(R.id.packageprice_et);
                        packagePriceEt.setHint("例如：80 等于80元");
                        AlertDialog alertDialog = new AlertDialog.Builder(PackageAddActivity.this)
                                .setTitle("自定总价  当前总价：" + finalSum).setView(view)
                                .setPositiveButton("确定", null)
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();

                        alertDialog.getButton(Dialog.BUTTON_POSITIVE)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if ("".equals(packageNameEt.getText().toString())) {

                                            packageNameEt.setError("不能为空！");


                                        }else if("".equals(packagePriceEt.getText().toString())){
                                            packagePriceEt.setError("不能为空！");
                                        } else {

                                            float sum = Float.valueOf(packagePriceEt.getText().toString());
                                            disheDcoument.setString("dishesName",packageNameEt.getText().toString());
                                            //保存数据库,设置套餐价格
                                            disheDcoument.setFloat("price", sum);
                                           MutableDocument mutableDocument = packageDcoument.toMutable();
                                           mutableDocument.getArray("dishesListId").addString(disheDcoument.getId());
                                            try {
                                                database.save(mutableDocument);
                                                database.save(disheDcoument);
                                            } catch (CouchbaseLiteException e) {
                                                e.printStackTrace();
                                            }
                                            dialog.dismiss();
                                            PackageAddActivity.this.finish();

                                        }
                                    }
                                });

                    }
                }).show();

            }
        });

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

            listItemView.disheNameTv.setText(document.getString("dishesName"));
            listItemView.dishePriceTv.setText("¥" + document.getFloat("price"));
            listItemView.dishesCk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    listMap.get(disheKindPosition)[position] = isChecked;

                }
            });
            listItemView.dishesCk.setChecked(listMap.get(disheKindPosition)[position]);

            return convertView;
        }

        public class ListItemView {

            TextView disheNameTv;
            TextView dishePriceTv;
            CheckBox dishesCk;

        }
    }

}

