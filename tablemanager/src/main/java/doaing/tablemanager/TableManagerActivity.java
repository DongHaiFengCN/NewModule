package doaing.tablemanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


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
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.util.ArrayList;
import java.util.List;

import bean.kitchenmanage.table.Table;
import doaing.mylibrary.MyApplication;
import doaing.tablemanager.adapter.AreaAdapter;
import tools.CDBHelper;
import tools.MyLog;
import tools.ToolUtil;
import view.BaseToobarActivity;

/**
 * @author donghaifeng
 */
@Route(path = "/tablemanager/TableManagerActivity")
public class TableManagerActivity extends BaseToobarActivity {
    int POSITION = 0;

    private Database database;
    private ListView areaLv;
    private AreaAdapter areaAdapter;
    private RecyclerView tableRc;
    private TableRecycleAdapter tableRecycleAdapter;
    EditText tableNameEt;
    EditText minmunNumberEt;
    EditText maxmunNumberEt;
    EditText minimunConsumptionEt;
    private String areaId;

    @Override
    protected int setMyContentView() {
        return R.layout.activity_table_manager_main;
    }

    @Override
    public void initData(Intent intent) {
        database = CDBHelper.getDatabase();
        setToolbarName("房间与桌位");
        areaLv = findViewById(R.id.area_lv);
        tableRc = findViewById(R.id.table_lv);
        tableRc.setLayoutManager(new GridLayoutManager(this, 2));
        tableRecycleAdapter = new TableRecycleAdapter();
        tableRc.setAdapter(tableRecycleAdapter);
        areaAdapter = new AreaAdapter(TableManagerActivity.this, database);
        areaLv.setAdapter(areaAdapter);

        areaLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                areaAdapter.changeSelected(pos);
                POSITION = pos;
                //设置餐桌适配器
                if (areaAdapter.getAreaId().size() == 0) {
                    tableRecycleAdapter.setArray(null);
                    return;
                }

                areaId = areaAdapter.getAreaId().get(pos);


                tableRecycleAdapter.setArray(areaId);


            }
        });
        setAreaListViewItemPosition(0);
    }

    @Override
    protected Toolbar setToolBarInfo() {

        return findViewById(R.id.toolbar);
    }

    public class TableRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<Document> docList;

        @Override
        public int getItemViewType(int position) {
            if (position == docList.size()) {

                return docList.size() + 1;
            }
            return super.getItemViewType(position);
        }

        private void setArray(String areaId) {
            docList = CDBHelper.getDocmentsByWhere(
                    Expression.property("className").equalTo(Expression.string("Table"))
                            .and(Expression.property("areaId").equalTo(Expression.string(areaId)))
                    , null);
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType < docList.size()) {
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
                final Document tableDoc = docList.get(position);
                String maxPersons = "";
                if ("" + tableDoc.getInt("maxPersons") != null) {
                    maxPersons = "" + tableDoc.getInt("maxPersons");
                }
                if (maxPersons.equals("0")) {
                    ((ViewHolder) holder).num.setText("");
                } else {
                    ((ViewHolder) holder).num.setText("" + maxPersons);
                }

                ((ViewHolder) holder).mTv.setText(tableDoc.getString("name"));
                ((ViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View view = getView();
                        tableNameEt.setText(tableDoc.getString("name"));
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
                                        //获取需要删除的餐桌id
                                        String id = tableDoc.getId();
                                        int count = docList.size();
                                        for (int i = 0; i < count; i++) {
                                            if (id.equals(docList.get(i))) {

                                                docList.remove(i);
                                                break;
                                            }
                                        }
                                        try {
                                            database.delete(tableDoc);
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
                                        } else {
                                            mutableDocument.setString("name", tableNameEt.getText().toString());
                                            if (TextUtils.isEmpty(maxmunNumberEt.getText().toString()) || Integer.valueOf(maxmunNumberEt.getText().toString()) == 0) {
                                                // mutableDocument.setInt("maxPersons", 0);
                                            } else {
                                                mutableDocument.setInt("maxPersons", Integer.valueOf(maxmunNumberEt.getText().toString()));
                                            }

                                            if (TextUtils.isEmpty(minmunNumberEt.getText().toString()) || Integer.valueOf(minmunNumberEt.getText().toString()) == 0) {
                                            } else {
                                                mutableDocument.setInt("minPersons", Integer.valueOf(minmunNumberEt.getText().toString()));
                                            }

                                            if (TextUtils.isEmpty(minimunConsumptionEt.getText().toString()) || Integer.valueOf(minimunConsumptionEt.getText().toString()) == 0) {
                                            } else {
                                                mutableDocument.setInt("minConsum", Integer.valueOf(minimunConsumptionEt.getText().toString()));
                                            }
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
            return docList == null ? 0 : docList.size() + 1;
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            TextView mTv, num;
            CardView cardView;

            private ViewHolder(View itemView) {
                super(itemView);
                mTv = itemView.findViewById(R.id.item_tablestate_name);
                cardView = itemView.findViewById(R.id.table_state_cardview);
                num = itemView.findViewById(R.id.item_tablestate_num);
            }
        }

        private class Item2ViewHolder extends RecyclerView.ViewHolder {
            CardView cardView;

            private Item2ViewHolder(View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.table_state_cardview);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (areaAdapter.getCount() == 1) {
                            Toast.makeText(TableManagerActivity.this, "先添加区域", Toast.LENGTH_LONG).show();
                            return;
                        }

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
                                //判断当前的区域是否是空的


                                //添加餐桌---------------
                                if ("".equals(tableNameEt.getText().toString())) {
                                    tableNameEt.setError("不能为空！");
                                } else {
                                    MutableDocument mutableDocument = new MutableDocument("Table." + ToolUtil.getUUID());
                                    mutableDocument.setString("channelId", ((MyApplication) getApplicationContext()).getCompany_ID());
                                    mutableDocument.setString("className", "Table");
                                    mutableDocument.setString("name", tableNameEt.getText().toString());
                                    mutableDocument.setString("dataType", "BaseData");
                                    //id
                                    mutableDocument.setInt("state", 0);
                                    mutableDocument.setInt("num", getMaxTableNumber());
                                    mutableDocument.setString("areaId", areaId);
                                    if (TextUtils.isEmpty(maxmunNumberEt.getText().toString()) || Integer.valueOf(maxmunNumberEt.getText().toString()) == 0) {
                                    } else {
                                        mutableDocument.setInt("maxPersons", Integer.valueOf(maxmunNumberEt.getText().toString()));
                                    }

                                    if (TextUtils.isEmpty(minmunNumberEt.getText().toString()) || Integer.valueOf(minmunNumberEt.getText().toString()) == 0) {
                                    } else {
                                        mutableDocument.setInt("minPersons", Integer.valueOf(minmunNumberEt.getText().toString()));
                                    }

                                    if (TextUtils.isEmpty(minimunConsumptionEt.getText().toString()) || Integer.valueOf(minimunConsumptionEt.getText().toString()) == 0) {
                                    } else {
                                        mutableDocument.setInt("minConsum", Integer.valueOf(minimunConsumptionEt.getText().toString()));
                                    }
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

    }

    public void setAreaListViewItemPosition(int position) {

        areaLv.performItemClick(areaLv.getChildAt(position), position, areaLv
                .getItemIdAtPosition(position));

    }

    @NonNull
    private View getView() {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.table_add_dialog, null);
        tableNameEt = view.findViewById(R.id.tablename_et);
        minmunNumberEt = view.findViewById(R.id.minimun_number);
        maxmunNumberEt = view.findViewById(R.id.maximum_number_et);
        minimunConsumptionEt = view.findViewById(R.id.minimun_consumption_et);
        return view;
    }

    /**
     * 得到当前最大的餐桌号
     *
     * @return 返回最大餐桌号+1
     */
    private int getMaxTableNumber() {
        int max = 0;

        Query query = QueryBuilder.select(SelectResult.expression(Meta.id), SelectResult.property("name"), SelectResult.property("num"))
                .from(DataSource.database(database)).where(Expression.property("className")
                        .equalTo(Expression.string("Table"))).
                        orderBy(Ordering.property("num").descending()).limit(Expression.intValue(1));
        try {

            ResultSet resultSet = query.execute();
            Result result = resultSet.next();

            if (result != null) {

                max = result.getInt("num");
            }


        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return (max + 1);
    }

}
