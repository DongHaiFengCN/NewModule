package doaing.tablemanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.couchbase.lite.Array;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.QueryChange;
import com.couchbase.lite.QueryChangeListener;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.util.ArrayList;
import java.util.List;

import doaing.MyApplication;
import doaing.tablemanager.adapter.AreaAdapter;
import tools.ToolUtil;
import view.BaseToobarActivity;

/**
 * @author donghaifeng
 */
@Route(path = "/table/TableManagerActivity")
public class TableManagerActivity extends BaseToobarActivity {

    int POSITION = 0;

    private Database database;
    private ListView areaLv;
    private AreaAdapter areaAdapter;
    private RecyclerView tableRc;
    private TableRecycleAdapter tableRecycleAdapter;
    Document areaDocment;
    EditText tableNameEt;
    EditText minmunNumberEt;
    EditText maxmunNumberEt;
    EditText minimunConsumptionEt;

    @Override
    protected int setMyContentView() {
        return R.layout.activity_table_manager_main;
    }

    @Override
    public void initData(Intent intent) {
        database = ((MyApplication) getApplicationContext()).getDatabase();
        setToolbarName("房间与桌位");
        areaLv = findViewById(R.id.area_lv);
        tableRc = findViewById(R.id.table_lv);
        tableRc.setLayoutManager(new GridLayoutManager(this, 3));
        tableRecycleAdapter = new TableRecycleAdapter();
        tableRc.setAdapter(tableRecycleAdapter);
        areaAdapter = new AreaAdapter(TableManagerActivity.this, database);
        areaLv.setAdapter(areaAdapter);
        areaLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                if (pos < areaAdapter.getCount()) {
                    areaAdapter.changeSelected(pos);
                    POSITION = pos;
                    //设置餐桌适配器
                    if (areaAdapter.getItem(pos) == null) {
                        tableRecycleAdapter.setArray(null);
                        return;
                    }
                    areaDocment = database.getDocument((String) areaAdapter.getItem(pos));

                    Array array = areaDocment.getArray("tableIDList");
                    tableRecycleAdapter.setArray(array);
                }
            }
        });
        setAreaListViewItemPosition(POSITION);


    }

    public void setAreaListViewItemPosition(int position) {


        if (areaAdapter.getCount() == 1) {
            tableRecycleAdapter.setArray(null);
            return;
        }
        areaLv.performItemClick(areaLv.getChildAt(position), position, areaAdapter.getItemId(position));
    }

    @Override
    protected Toolbar setToolBarInfo() {

        return findViewById(R.id.toolbar);
    }

    public class TableRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
       Array array;

        @Override
        public int getItemViewType(int position) {
            if (position == array.count()) {

                return array.count() + 1;
            }
            return super.getItemViewType(position);
        }

        public void setArray(Array array) {
            this.array = array;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType < array.count()) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_table, parent, false);
                return new ViewHolder(v);
            } else {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.table_add_item_rcy, parent, false);
                return new Item2ViewHolder(v);
            }

        }

        /**
         * 修改餐桌信息的方法
         *
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            // 绑定数据
            if (holder instanceof ViewHolder) {
                final Document tableDoc = database.getDocument(array.getString(position));
            

                ((ViewHolder) holder).mTv.setText(tableDoc.getString("tableName"));
                ((ViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View view = getView();
                        tableNameEt.setText(tableDoc.getString("tableName"));
                        maxmunNumberEt.setText(String.valueOf(tableDoc.getInt("maxPersons")));
                        minmunNumberEt.setText(String.valueOf(tableDoc.getInt("minPersons")));
                        minimunConsumptionEt.setText(String.valueOf(tableDoc.getInt("minConsum")));
                        //查看编辑餐桌
                        final AlertDialog alertDialog = new AlertDialog.Builder(TableManagerActivity.this)
                                .setTitle("修改餐桌信息").setView(view).setPositiveButton("确认修改", null)
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).setNeutralButton("删除餐桌", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //获取房间信息
                                        MutableArray array = areaDocment.getArray("tableIDList").toMutable();

                                        //获取需要删除的餐桌id
                                        String id = tableDoc.getId();
                                        int count = array.count();
                                        for (int i = 0; i < count; i++) {
                                            if (id.equals(array.getString(i))) {
                                                array.remove(i);
                                                break;
                                            }
                                        }
                                        try {
                                            database.delete(tableDoc);
                                            database.save(areaDocment.toMutable());
                                        } catch (CouchbaseLiteException e) {
                                            e.printStackTrace();
                                        }
                                        setAreaListViewItemPosition(POSITION);
                                    }
                                }).show();
                        alertDialog.getButton(Dialog.BUTTON_POSITIVE)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        MutableDocument mutableDocument = tableDoc.toMutable();
                                        //修改餐桌---------------
                                        if ("".equals(tableNameEt.getText().toString())) {
                                            tableNameEt.setError("不能为空！");
                                        } else if ("".equals(minmunNumberEt.getText().toString())) {
                                            minmunNumberEt.setError("不能为空！");
                                        } else if ("".equals(maxmunNumberEt.getText().toString())) {
                                            maxmunNumberEt.setError("不能为空！");
                                        } else if ("".equals(minimunConsumptionEt.getText().toString())) {
                                            minimunConsumptionEt.setError("不能为空！");
                                        } else {
                                            mutableDocument.setString("tableName", tableNameEt.getText().toString());
                                            mutableDocument.setInt("maxPersons", Integer.valueOf(maxmunNumberEt.getText().toString()));
                                            mutableDocument.setInt("minPersons", Integer.valueOf(minmunNumberEt.getText().toString()));
                                            mutableDocument.setInt("minConsum", Integer.valueOf(minimunConsumptionEt.getText().toString()));
                                            try {
                                                database.save(mutableDocument);
                                            } catch (CouchbaseLiteException e) {
                                                e.printStackTrace();
                                            }
                                            setAreaListViewItemPosition(POSITION);
                                            alertDialog.dismiss();
                                        }
                                    }
                                });

                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return array == null ? 0 : array.count() + 1;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView mTv;
            CardView cardView;

            public ViewHolder(View itemView) {
                super(itemView);
                mTv = itemView.findViewById(R.id.item_tablestate_name);
                cardView = itemView.findViewById(R.id.table_state_cardview);
            }
        }

        public class Item2ViewHolder extends RecyclerView.ViewHolder {
            CardView cardView;

            public Item2ViewHolder(View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.table_state_cardview);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final AlertDialog alertDialog = new AlertDialog.Builder(TableManagerActivity.this)
                                .setView(getView()).setTitle("添加餐桌")
                                .setPositiveButton("确定", null).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();
                        alertDialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //添加餐桌---------------
                                if ("".equals(tableNameEt.getText().toString())) {
                                    tableNameEt.setError("不能为空！");
                                } else if ("".equals(minmunNumberEt.getText().toString())) {
                                    minmunNumberEt.setError("不能为空！");
                                } else if ("".equals(maxmunNumberEt.getText().toString())) {
                                    maxmunNumberEt.setError("不能为空！");
                                } else if ("".equals(minimunConsumptionEt.getText().toString())) {
                                    minimunConsumptionEt.setError("不能为空！");
                                } else {

                                    MutableDocument mutableDocument = new MutableDocument("TableC." + ToolUtil.getUUID());
                                    mutableDocument.setString("channelId", ((MyApplication) getApplicationContext()).getCompany_ID());
                                    mutableDocument.setString("className", "TableC");
                                    mutableDocument.setString("tableName", tableNameEt.getText().toString());
                                    mutableDocument.setString("dataType", "BaseData");
                                    //id
                                    mutableDocument.setInt("state", 0);
                                    mutableDocument.setString("tableNum", getMaxTableNum());
                                    mutableDocument.setString("areaId", areaDocment.getId());
                                    mutableDocument.setInt("maxPersons", Integer.valueOf(maxmunNumberEt.getText().toString()));
                                    mutableDocument.setInt("minPersons", Integer.valueOf(minmunNumberEt.getText().toString()));
                                    mutableDocument.setInt("minConsum", Integer.valueOf(minimunConsumptionEt.getText().toString()));

                                    //新版方法
                                    MutableDocument areaDoc = areaDocment.toMutable();
                                    Log.e("DOAING", "查找：" + areaDoc.getString("areaName"));
                                    Log.e("DOAING", "保存tableId：" + mutableDocument.getId());

                                    MutableArray mutableArray = areaDoc.getArray("tableIDList");
                                    mutableArray.addString(mutableDocument.getId());
                                    try {
                                        database.save(mutableDocument);
                                        database.save(areaDoc);
                                    } catch (CouchbaseLiteException e) {
                                        e.printStackTrace();

                                    }
                                    setAreaListViewItemPosition(POSITION);
                                    alertDialog.dismiss();
                                }
                            }
                        });
                    }
                });
            }
        }

    }

    @NonNull
    private View getView() {
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.table_add_dialog, null);
        tableNameEt = view.findViewById(R.id.tablename_et);
        minmunNumberEt = view.findViewById(R.id.minimun_number);
        maxmunNumberEt = view.findViewById(R.id.maximum_number_et);
        minimunConsumptionEt = view.findViewById(R.id.minimun_consumption_et);
        return view;
    }

    private String getMaxTableNum() {
        List<Document> documentList = new ArrayList<>();
        String maxNum = null;
        Query query = QueryBuilder.select(SelectResult.expression(Meta.id))
                .from(DataSource.database(database)).where(Expression.property("className")
                        .equalTo(Expression.string("TableC"))).orderBy(Ordering.property("tableNum").descending());
        try {
            ResultSet resultSet = query.execute();
            Result row;
            while ((row = resultSet.next()) != null) {
                String id = row.getString(0);
                Document doc = database.getDocument(id);
                documentList.add(doc);

            }
        } catch (CouchbaseLiteException e) {
            android.util.Log.e("getDocmentsByClass", "Exception=", e);
        }

        if (documentList.size() > 0) {
            Document document = documentList.get(0);
            maxNum = document.getString("tableNum");
            int temp = Integer.valueOf(maxNum).intValue();
            maxNum = String.format("%3d", temp + 1).replace(" ", "0");
        } else if (documentList.size() == 0) {
            maxNum = "001";
        }
        return maxNum;
    }
}
