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
import com.couchbase.lite.Function;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.LiveQueryChange;
import com.couchbase.lite.LiveQueryChangeListener;
import com.couchbase.lite.Log;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.Query;
import com.couchbase.lite.ReadOnlyDictionary;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import doaing.mylibrary.MyApplication;
import doaing.tablemanager.adapter.AreaAdapter;
import tools.ToolUtil;
import view.BaseToobarActivity;

/**
 * @author donghaifeng
 */
@Route(path = "/table/TableManagerActivity")
public class TableManagerActivity extends BaseToobarActivity {

    int POSITION = 0;
    private Document areaDoc;
    private Database database;
    private List<Document> areaList;
    private ListView areaLv;
    private AreaAdapter areaAdapter;
    private RecyclerView tableRc;
    private TableRecycleAdapter tableRecycleAdapter;
    EditText tableIdEt;
    EditText tableNameEt;
    EditText minmunNumberEt;
    EditText maxmunNumberEt;
    EditText minimunConsumptionEt;

    @Override
    protected int setMyContentView() {
        return R.layout.activity_table_manager_main;
    }

    @Override
    public void initData(Intent intent){
        database = ((MyApplication) getApplicationContext()).getDatabase();

        setToolbarName("房间与桌位");
        areaLv = findViewById(R.id.area_lv);
        tableRc = findViewById(R.id.table_lv);
        tableRc.setLayoutManager(new GridLayoutManager(this, 3));
        tableRecycleAdapter = new TableRecycleAdapter();
        tableRc.setAdapter(tableRecycleAdapter);
        areaList = new ArrayList<>();
        areaAdapter = new AreaAdapter(areaList, TableManagerActivity.this, database);
        areaLv.setAdapter(areaAdapter);
        areaQuery();

        areaLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                if(pos<areaList.size()){
                    areaAdapter.changeSelected(pos);
                    areaDoc = database.getDocument(areaList.get(pos).getId());

                    POSITION = pos;
                    //设置餐桌适配器
                    Array array = areaDoc.getArray("tableIDList");
                    tableRecycleAdapter.setArray(array);
                }
            }
        });
        setAreaListViewItemPosition(POSITION);


    }

    public void setAreaListViewItemPosition(int position) {


        if (areaList.size() == 0) {
            tableRecycleAdapter.setArray(null);
            return;
        }
        areaLv.performItemClick(areaLv.getChildAt(position), position, areaAdapter.getItemId(position));
    }

    @Override
    protected Toolbar setToolBarInfo() {

        return findViewById(R.id.toolbar);
    }

    /**
     * 动态监听dishesKind更新信息
     */
    private void areaQuery() {

        //动态监听DisheKind信息
        LiveQuery query = listsLiveQuery();
        query.addChangeListener(new LiveQueryChangeListener() {
            @Override
            public void changed(LiveQueryChange change) {
                if (!areaList.isEmpty()) {
                    areaList.clear();
                }
                ResultSet rows = change.getRows();
                Result row = null;
                while ((row = rows.next()) != null) {

                    String id = row.getString(0);
                    Document doc = database.getDocument(id);
                    areaList.add(doc);
                }

                areaAdapter.notifyDataSetChanged();
            }
        });
        query.run();
    }

    private LiveQuery listsLiveQuery() {
        return Query.select(SelectResult.expression(Expression.meta().getId())
                , SelectResult.expression(Expression.property("areaName")))
                .from(DataSource.database(database))
                .where(Expression.property("className").equalTo("AreaC"))
                .orderBy(Ordering.property("areaNum").ascending())
                .toLive();
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

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            // 绑定数据
            if (holder instanceof ViewHolder) {

                String id = array.getString(position);
                final Document document = database.getDocument(id);
                ((ViewHolder) holder).mTv.setText(document.getString("tableName"));
                ((ViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View view = getView();
                        tableNameEt.setText(document.getString("tableName"));
                        maxmunNumberEt.setText(String.valueOf(document.getInt("maxPersons")));
                        minmunNumberEt.setText(String.valueOf(document.getInt("minPersons")));
                        minimunConsumptionEt.setText(String.valueOf(document.getInt("minConsum")));
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
                                        Array array = areaDoc.getArray("tableIDList");
                                        String id = document.getId();
                                        int count = array.count();
                                        for (int i = 0; i < count; i++) {
                                            if (id.equals(array.getString(i))) {
                                                array.remove(i);
                                                break;
                                            }
                                        }
                                        try {
                                            database.delete(document);
                                            database.save(areaDoc);
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
                                            document.setString("tableName", tableNameEt.getText().toString());
                                            document.setInt("maxPersons", Integer.valueOf(maxmunNumberEt.getText().toString()));
                                            document.setInt("minPersons", Integer.valueOf(minmunNumberEt.getText().toString()));
                                            document.setInt("minConsum", Integer.valueOf(minimunConsumptionEt.getText().toString()));
                                            try {
                                                database.save(document);
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
                                    Document document = new Document("TableC." + ToolUtil.getUUID());
                                    document.setString("channelId", ((MyApplication) getApplicationContext()).getCompany_ID());
                                    document.setString("className", "TableC");
                                    document.setString("tableName", tableNameEt.getText().toString());
                                    //id
                                    document.setInt("state",0);
                                    document.setString("tableNum",getMaxTableNum());
                                    document.setString("areaId", areaDoc.getId());
                                    document.setInt("maxPersons", Integer.valueOf(maxmunNumberEt.getText().toString()));
                                    document.setInt("minPersons", Integer.valueOf(minmunNumberEt.getText().toString()));
                                    document.setInt("minConsum", Integer.valueOf(minimunConsumptionEt.getText().toString()));
                                    areaDoc.getArray("tableIDList").addString(document.getId());

                                    try {
                                        database.save(document);
                                        database.save(areaDoc);
                                    } catch (CouchbaseLiteException e) {
                                        e.printStackTrace();

                                    }
                                    setAreaListViewItemPosition(POSITION);
                                    alertDialog.dismiss();
                                    Log.e("DOAING","餐桌号："+document.getString("tableNum"));
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

    private String getMaxTableNum(){
        List<Document> documentList = new ArrayList<>();
        String maxNum=null;
        Query query= Query.select(SelectResult.expression(Expression.meta().getId()))
                .from(DataSource.database(database)).where(Expression.property("className")
                .equalTo("TableC")).orderBy(Ordering.property("tableNum").descending());
        try {
            ResultSet resultSet = query.run();
            Result row;
            while ((row = resultSet.next()) != null) {
                String id = row.getString(0);
                Document doc = database.getDocument(id);
                documentList.add(doc);

            }
        } catch (CouchbaseLiteException e) {
            android.util.Log.e("getDocmentsByClass", "Exception=", e);
        }

        if(documentList.size()>0) {
            Document  document = documentList.get(0);
            maxNum= document.getString("tableNum");
            int temp = Integer.valueOf(maxNum).intValue();
            maxNum = String.format("%3d", temp+1).replace(" ", "0");
        } else if (documentList.size()==0){
            maxNum="001";
        }
        return maxNum;
    }
}
