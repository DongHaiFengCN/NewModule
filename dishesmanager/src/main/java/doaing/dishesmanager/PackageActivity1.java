package doaing.dishesmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

import doaing.dishesmanager.view.MySwipeListLayout;
import doaing.mylibrary.MyApplication;
import tools.CDBHelper;
import tools.ToolUtil;
import view.BaseToobarActivity;

public class PackageActivity1 extends BaseToobarActivity {
    private SearchView.SearchAutoComplete mSearchAutoComplete;
    private Database database;
    private List<String> list;
    private ListView packageLv;
    private ListAdapter listAdapter;

    @Override
    protected int setMyContentView() {
        return R.layout.activity_package1;
    }

    @Override
    public void initData(Intent intent) {
        setToolbarName("套餐索引");
        database = CDBHelper.getDatabase();
        packageLv = findViewById(R.id.package_lv);
        initList();


    }

    @Override
    protected Toolbar setToolBarInfo() {
        return findViewById(R.id.toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toobar_add, menu);
        final MenuItem addhItem = menu.findItem(R.id.action_add);
        addhItem.setVisible(false);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) searchItem.getActionView();
        mSearchAutoComplete = mSearchView.findViewById(R.id.search_src_text);
        ImageView searchButton = mSearchView.findViewById(R.id.search_button);
        searchButton.setImageResource(R.mipmap.icon_add);

        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addhItem.setVisible(true);
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                addhItem.setVisible(false);
                return false;
            }
        });
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

                    addPackageInfo(mSearchAutoComplete);

                }
                return false;
            }
        });
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            addPackageInfo(mSearchAutoComplete);
        }
        return super.onOptionsItemSelected(item);
    }

    private void addPackageInfo(SearchView.SearchAutoComplete mSearchAutoComplete) {
        //1.添加数据到数据库
        MutableDocument document = new MutableDocument("DishesKind." + ToolUtil.getUUID());
        document.setString("channelId", ((MyApplication) getApplicationContext()).getCompany_ID());
        document.setString("className", "DishesKind");
        document.setString("name", mSearchAutoComplete.getText().toString());
        document.setString("dataType", "BaseData");
        //document.setArray("dishesListId", new MutableArray());
        try {
            database.save(document);
            list.add(document.getId());
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        mSearchAutoComplete.setText("");

        listAdapter.notifyDataSetChanged();
    }

    private void initList() {
        list = new ArrayList<>();

        Query query = QueryBuilder.select(SelectResult.expression(Meta.id))
                .from(DataSource.database(database))
                .where(Expression.property("className").equalTo(Expression.string("DishesKindC"))
                        );

        ResultSet results = null;

        try {
            results = query.execute();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        Result row;
        while ((row = results.next()) != null) {

            list.add(row.getString(0));

        }

        listAdapter = new ListAdapter();
        packageLv.setAdapter(listAdapter);


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

            final Document document = database.getDocument(list.get(arg0));

            if (view == null) {
                view = LayoutInflater.from(getApplicationContext()).inflate(
                        R.layout.content_package, null);
            }
            final TextView tvName = view.findViewById(R.id.package_tv);

            tvName.setText(document.getString("name"));


            final MySwipeListLayout sllMain = view.findViewById(R.id.package_sll_main);

            TextView tvEdit = view.findViewById(R.id.entery_tv);
            tvEdit.setText("进入");

            TextView tvDelete = view.findViewById(R.id.delete_tv);

            tvEdit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {


                    AlertDialog.Builder a = new AlertDialog.Builder(PackageActivity1.this);

                    a.setTitle("选择套餐方式");
                    a.setPositiveButton("二级套餐", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent  intent = new Intent(PackageActivity1.this,SecondPackageActivity.class);
                            intent.putExtra("id",document.getId());
                            startActivity(intent);

                        }
                    });
                    a.setNegativeButton("菜品", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(PackageActivity1.this, PackageAddActivity1.class);
                            intent.putExtra("id", document.getId());
                            startActivity(intent);
                        }
                    });

                    a.show();
                }
            });
            tvDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    new AlertDialog.Builder(PackageActivity1.this).setTitle("删除一级套餐？")
                            .setMessage(tvName.getText().toString())
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    sllMain.setStatus(MySwipeListLayout.Status.Close, true);

                                    try {
                                        database.delete(document);

                                    } catch (CouchbaseLiteException e) {
                                        e.printStackTrace();
                                    }
                                    list.remove(document.getId());
                                    listAdapter.notifyDataSetChanged();

                                    //删除二级套餐，dishe 的数据



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
