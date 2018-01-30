package doaing.dishesmanager;

import android.content.Context;
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
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import module.MyApplication;
import view.BaseToobarActivity;

public class PackageActivity extends BaseToobarActivity {
    private Map<String, List<String>> dataset = new HashMap<>();
    private String[] parentList = new String[]{"春季套餐", "夏季套餐", "秋季套餐", "冬季套餐"};
    private List<String> childrenList1 = new ArrayList<>();
    private List<String> childrenList2 = new ArrayList<>();
    private List<String> childrenList3 = new ArrayList<>();
    private List<String> childrenList4 = new ArrayList<>();

    private Database database;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.package_explv)
    ExpandableListView packageExplv;

    @Override
    protected int setMyContentView() {
        return R.layout.activity_package;
    }

    @Override
    protected Toolbar setToolBarInfo() {
        return toolbar;
    }

    @Override
    public void initData(Intent intent) {

        database = ((MyApplication) getApplicationContext()).getDatabase();

        initialData();

        packageExplv.setAdapter(new MyExpandableListViewAdapter());


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
                    Document document = new Document();
                    document.setString("channelId", "gysz");
                    document.setString("className", "DishesTasteC");
                    document.setString("tasteName", mSearchAutoComplete.getText().toString());
                    try {
                        database.save(document);
                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    }
                    mSearchAutoComplete.setText("");
                }

                return false;
            }
        });
        return true;

    }

    private void initialData() {

        childrenList1.add(parentList[0] + "-" + "1");
        childrenList1.add(parentList[0] + "-" + "2");
        childrenList1.add(parentList[0] + "-" + "3");
        childrenList2.add(parentList[1] + "-" + "1");
        childrenList2.add(parentList[1] + "-" + "2");
        childrenList2.add(parentList[1] + "-" + "3");
        childrenList3.add(parentList[2] + "-" + "1");
        childrenList3.add(parentList[2] + "-" + "2");
        childrenList3.add(parentList[2] + "-" + "3");
        childrenList4.add(parentList[3] + "-" + "1");
        childrenList4.add(parentList[3] + "-" + "2");
        childrenList4.add(parentList[3] + "-" + "3");
        dataset.put(parentList[0], childrenList1);
        dataset.put(parentList[1], childrenList2);
        dataset.put(parentList[2], childrenList3);
        dataset.put(parentList[3], childrenList4);


    }

    private class MyExpandableListViewAdapter extends BaseExpandableListAdapter {


        //  获得某个父项的某个子项
        @Override
        public Object getChild(int parentPos, int childPos) {
            return dataset.get(parentList[parentPos]).get(childPos);
        }

        //  获得父项的数量
        @Override
        public int getGroupCount() {
            return dataset.size();
        }

        //  获得某个父项的子项数目
        @Override
        public int getChildrenCount(int parentPos) {
            return dataset.get(parentList[parentPos]).size();
        }

        //  获得某个父项
        @Override
        public Object getGroup(int parentPos) {
            return dataset.get(parentList[parentPos]);
        }

        //  获得某个父项的id
        @Override
        public long getGroupId(int parentPos) {
            return parentPos;
        }

        //  获得某个父项的某个子项的id
        @Override
        public long getChildId(int parentPos, int childPos) {
            return childPos;
        }

        //  按函数的名字来理解应该是是否具有稳定的id，这个方法目前一直都是返回false，没有去改动过
        @Override
        public boolean hasStableIds() {
            return false;
        }


        //  获得父项显示的view
        @Override
        public View getGroupView(int parentPos, boolean b, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) PackageActivity
                        .this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.parent_item, null);
            }
            view.setTag(R.layout.parent_item, parentPos);
            view.setTag(R.layout.child_item, -1);
            TextView text = view.findViewById(R.id.parent_title);
            text.setText(parentList[parentPos]);
     /*       if (dataset.get(parentList[parentPos]) == null) {

                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(PackageActivity.this, "!!", Toast.LENGTH_LONG).show();
                    }
                });
            }*/
            return view;
        }

        //  获得子项显示的view
        @Override
        public View getChildView(int parentPos, int childPos, boolean b, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) PackageActivity
                        .this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.child_item, null);
            }
            view.setTag(R.layout.parent_item, parentPos);
            view.setTag(R.layout.child_item, childPos);
            TextView text = view.findViewById(R.id.child_title);
            text.setText(dataset.get(parentList[parentPos]).get(childPos));
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(PackageActivity.this, "点到了内置的textview", Toast.LENGTH_SHORT).show();
                }
            });
            return view;
        }

        //  子项是否可选中，如果需要设置子项的点击事件，需要返回true
        @Override
        public boolean isChildSelectable(int i, int i1) {
            return false;
        }
    }
}
