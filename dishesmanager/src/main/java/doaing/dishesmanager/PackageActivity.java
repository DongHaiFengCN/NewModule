
package doaing.dishesmanager;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.ExpandableListView;
import android.widget.ImageView;
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
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

import doaing.dishesmanager.adapter.PackageManagerExpandableAdapter;
import doaing.mylibrary.MyApplication;
import tools.CDBHelper;
import tools.ToolUtil;
import view.BaseToobarActivity;

public class PackageActivity extends BaseToobarActivity {
    private Query query;
    private Database database;
    private Map<String, List<Document>> dataSet = new HashMap<>();
    private List<Document> groupList = new ArrayList<>();
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.package_explv)
    ExpandableListView packageExplv;
    PackageManagerExpandableAdapter expandableAdapter;

    @Override
    protected int setMyContentView() {
        return R.layout.activity_package;
    }

    @Override
    protected Toolbar setToolBarInfo() {
        return toolbar;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData(null);
    }

    @Override
    public void initData(Intent intent) {
        database = CDBHelper.getDatabase();
        query = QueryBuilder.select(SelectResult.expression(Meta.id))
                .from(DataSource.database(database))
                .where(Expression.property("className").equalTo(Expression.string("DishesKindC"))
                        .and(Expression.property("setMenu").equalTo(Expression.booleanValue(true))));
        setToolbarName("套餐管理");
        initList();
        expandableAdapter = new PackageManagerExpandableAdapter(groupList, dataSet, PackageActivity.this);
        packageExplv.setAdapter(expandableAdapter);

    }

    private void initList() {
        ResultSet results = null;

        if (groupList.size() > 0) {
            groupList.clear();
        }
        try {
            results = query.execute();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        Result row;
        while ((row = results.next()) != null) {
            String id = row.getString(0);
            Document doc = database.getDocument(id);
            groupList.add(doc);
        }

        for (int i = 0; i < groupList.size(); i++) {
            List<Document> childList;
            Array array = groupList.get(i).getArray("dishesListId");
            if (array != null) {
                childList = new ArrayList<>();
            } else {
                dataSet.put(groupList.get(i).getString("kindName"), new ArrayList<Document>());
                continue;
            }


            for (int j = 0; j < array.count(); j++) {
                childList.add(database.getDocument(array.getString(j)));
            }

            dataSet.put(groupList.get(i).getString("kindName"), childList);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.taste, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) searchItem.getActionView();
        final SearchView.SearchAutoComplete mSearchAutoComplete = mSearchView.findViewById(R.id.search_src_text);
        ImageView searchButton = mSearchView.findViewById(R.id.search_button);
        searchButton.setImageResource(R.mipmap.icon_add);
        //设置背景颜色
        mSearchAutoComplete.setBackgroundColor(ContextCompat.getColor(this, R.color.md_white));
        //设置Hint文字颜色
        mSearchAutoComplete.setHintTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        mSearchView.setQueryHint("添加一级套餐");
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

                    //1.添加数据到数据库
                    MutableDocument document = new MutableDocument("DishesKindC." + ToolUtil.getUUID());
                    document.setString("channelId", ((MyApplication) getApplicationContext()).getCompany_ID());
                    document.setString("className", "DishesKindC");
                    document.setString("kindName", mSearchAutoComplete.getText().toString());
                    document.setBoolean("setMenu", true);
                    document.setArray("dishesListId", new MutableArray());
                    try {
                        database.save(document);
                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    }
                    initList();
                    expandableAdapter.notifyDataSetChanged();
                    mSearchAutoComplete.setText("");
                }

                return false;
            }
        });
        return true;

    }


}

