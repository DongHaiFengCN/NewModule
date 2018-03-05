package doaing.dishesmanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.couchbase.lite.Array;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.QueryChange;
import com.couchbase.lite.QueryChangeListener;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import doaing.dishesmanager.view.MySwipeListLayout;
import doaing.mylibrary.MyApplication;
import tools.CDBHelper;
import tools.MyLog;
import tools.ToolUtil;
import view.BaseToobarActivity;


/**
 * @author donghaifeng
 * @Data 2018/1/19
 */

public class DishesKindActivity extends BaseToobarActivity {


    /**
     * 修改为Document
     */

    private List<Document> list = new ArrayList<>();

    private Database database;

    ListAdapter listAdapter;

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.disheskind_lv)
    ListView dishesKindLv;

    @Override
    protected int setMyContentView() {
        return R.layout.activity_dishes_kind;
    }

    @Override
    protected Toolbar setToolBarInfo() {
        return toolbar;
    }

    @Override
    public void initData(Intent intent) {
        setToolbarName("菜类管理");
        initList();
        listAdapter = new ListAdapter();

        dishesKindLv.setAdapter(listAdapter);

    }


    /**
     * 加载数据库数据
     */

    private void initList() {
        listAdapter = new ListAdapter();
        dishesKindLv.setAdapter(listAdapter);
        database = CDBHelper.getDatabase();
        Query query = QueryBuilder.select(SelectResult.expression(Meta.id),SelectResult.expression(Expression.property("kindName")))
                .from(DataSource.database(database))
                .where(Expression.property("className").equalTo(Expression.string("DishesKindC")));
        query.addChangeListener(new QueryChangeListener() {
            @Override
            public void changed(QueryChange change) {

                if (!list.isEmpty()) {
                    list.clear();
                }
                ResultSet rows = change.getResults();
                Result row;
                while ((row = rows.next()) != null) {

                    String id = row.getString(0);
                    Document doc = database.getDocument(id);
                    list.add(doc);

                }


                listAdapter.notifyDataSetChanged();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.taste, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) searchItem.getActionView();
        final SearchView.SearchAutoComplete mSearchAutoComplete = mSearchView.findViewById(R.id.search_src_text);
        ImageView searchButton = mSearchView.findViewById(R.id.search_button);
        searchButton.setImageResource(R.mipmap.icon_add);
        mSearchAutoComplete.setBackgroundColor(ContextCompat.getColor(this, R.color.md_white));
        //设置Hint文字颜色
        mSearchAutoComplete.setHintTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        mSearchView.setQueryHint("添加菜类或一级套餐");
        //设置输入文字颜色
        mSearchAutoComplete.setTextColor(ContextCompat.getColor(this, R.color.md_blue_grey_700));
        //设置是否显示搜索框展开时的提交按钮
        mSearchView.setSubmitButtonEnabled(false);
        mSearchAutoComplete.setImeOptions(EditorInfo.IME_ACTION_SEND);
        mSearchAutoComplete.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        mSearchAutoComplete.setSingleLine(true);
        mSearchAutoComplete.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {

                    new AlertDialog.Builder(DishesKindActivity.this)
                            .setTitle("选择添加类型")
                            .setPositiveButton("菜类", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    saveall(mSearchAutoComplete.getText().toString(), false);
                                    mSearchAutoComplete.setText("");
                                }
                            })
                            .setNegativeButton("一级套餐", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    saveall(mSearchAutoComplete.getText().toString(), true);
                                    mSearchAutoComplete.setText("");
                                }
                            })
                            .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();

                }


                return false;
            }
        });
        return true;
    }

    /**
     * @param kindName 菜类或一级套餐名称
     * @param flag     true 一级套餐 false 菜类
     */
    private void saveall(String kindName, boolean flag) {
        MutableDocument document = new MutableDocument("DishesKindC." + ToolUtil.getUUID());
        document.setString("channelId", ((MyApplication)getApplicationContext()).getCompany_ID());
        MyLog.e("DishesKind","channeldId="+((MyApplication) getApplicationContext()).getCompany_ID());
        document.setString("className", "DishesKindC");
        document.setBoolean("setMenu", flag);
        document.setString("kindName", kindName);
        document.setArray("dishesListId", new MutableArray());
        try {
            database.save(document);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
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

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int arg0, View view, ViewGroup arg2) {
            if (view == null) {
                view = LayoutInflater.from(DishesKindActivity.this).inflate(
                        R.layout.slip_list_item, null);
            }
            final TextView tvName = view.findViewById(R.id.tv_name);
            tvName.setText(list.get(arg0).getString("kindName"));
            final MySwipeListLayout sllMain = view.findViewById(R.id.sll_main);
            TextView tvEdit = view.findViewById(R.id.tv_edit);
            TextView tvDelete = view.findViewById(R.id.tv_delete);

            tvEdit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    final EditText editText = new EditText(DishesKindActivity.this);

                    final AlertDialog alertDialog = new AlertDialog.Builder(DishesKindActivity.this).setTitle("修改菜类或套餐名称")
                            .setView(editText)
                            .setNegativeButton("确定", null)
                            .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();


                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if ("".equals(editText.getText().toString())) {

                                editText.setError("不能为空！");

                            } else {


                                MutableDocument mutableDocument = list.get(arg0).toMutable();

                                mutableDocument.setString("kindName", editText.getText().toString());

                                try {
                                    database.save(mutableDocument);
                                    alertDialog.dismiss();
                                } catch (CouchbaseLiteException e) {
                                    e.printStackTrace();
                                }

                            }

                        }
                    });
                    sllMain.setStatus(MySwipeListLayout.Status.Close, true);


                    notifyDataSetChanged();

                }
            });
            tvDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    new AlertDialog.Builder(DishesKindActivity.this).setTitle("删除菜类以及对应的菜品")
                            .setMessage(tvName.getText().toString())
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    sllMain.setStatus(MySwipeListLayout.Status.Close, true);

                                    try {
                                        Document document = list.get(arg0);
                                        database.delete(document);

                                        //删除菜品
                                        Array array = document.getArray("dishesListId");

                                        if (array == null) {
                                            return;
                                        }
                                        for (int i = 0; i < array.count(); i++) {

                                            database.delete(database.getDocument(array.getString(i)));

                                        }

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
            });
            return view;
        }

    }
}
