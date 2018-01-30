package doaing.dishesmanager;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.couchbase.lite.Array;
import com.couchbase.lite.Blob;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.LiveQueryChange;
import com.couchbase.lite.LiveQueryChangeListener;
import com.couchbase.lite.Log;
import com.couchbase.lite.Query;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import module.MyApplication;
import view.BaseToobarActivity;

public class DishesActivity extends BaseToobarActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.dishes_lv)
    ListView dishes_Lv;

    @BindView(R.id.disheskind_lv)
    ListView disheskind_Lv;

    private DishesKindAdapter dishesKindAdapter;
    private List dishesKindList = new ArrayList<Document>();

    private DishesAdapter dishesAdapter;
    private List dishesList = new ArrayList<Document>();
    private LiveQuery query;

    private int dishePosition = 0;

    private int kindPosition = 0;
    private Database database;

    @Override
    protected int setMyContentView() {
        return R.layout.activity_dishes;
    }


    @Override
    public void initData(final Intent intent) {

        EventBus.getDefault().register(this);

        database = ((MyApplication) getApplicationContext()).getDatabase();
        initkindList();

        dishes_Lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                //0 .停止滚动 1.用户使用触摸屏滚动，手指仍在屏幕上 2.用户以前使用触摸滚动，并进行了一次投掷。动画现在滑行到停止。
                //  MyLog.e(scrollState+"");

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                //上滑动隐藏 &&下滑动显示
                if (dishePosition < firstVisibleItem) {

                    fab.hide();

                } else if (dishePosition > firstVisibleItem) {
                    fab.show();
                }

                dishePosition = firstVisibleItem;

            }
        });

        dishes_Lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Document dishes = ((Document) dishesList.get(position));


                Intent intent1 = new Intent(DishesActivity.this, DisheEditActivity.class);
                intent1.putExtra("dishes", dishes.getId());
                intent1.putExtra("position", kindPosition);
                startActivity(intent1);


            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DishesActivity.this, DisheAddActivity.class));
            }
        });
    }

    @Override
    protected Toolbar setToolBarInfo() {
        return toolbar;
    }

    /**
     * 菜类的适配器
     */
    public class DishesKindAdapter extends BaseAdapter {

        private int mSelect = 0; //选中项

        private List<Document> names;

        private DishesKindAdapter(List<Document> names) {

            this.names = names;
        }

        @Override
        public int getCount() {
            return names == null ? 0 : names.size();
        }

        @Override
        public Object getItem(int i) {
            return names.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            LayoutInflater listContainerLeft;
            listContainerLeft = LayoutInflater.from(DishesActivity.this);
            ListItemView listItemView = null;
            if (view == null) {
                listItemView = new ListItemView();
                view = listContainerLeft.inflate(R.layout.disheskind_list_item, null);
                listItemView.tv_title = view.findViewById(R.id.title);
                listItemView.imageView = view.findViewById(R.id.imageView);
                view.setTag(listItemView);
            } else {
                listItemView = (ListItemView) view.getTag();
            }
            if (mSelect == i) {
                view.setBackgroundResource(R.color.md_grey_50);  //选中项背景
                listItemView.imageView.setVisibility(View.VISIBLE);
            } else {
                view.setBackgroundResource(R.color.md_grey_100);  //其他项背景
                listItemView.imageView.setVisibility(View.INVISIBLE);
            }

            listItemView.tv_title.setText(names.get(i).getString("kindName"));

            return view;

        }

        public void changeSelected(int positon) { //刷新方法
            mSelect = positon;
            notifyDataSetChanged();
        }

        class ListItemView {

            TextView tv_title;
            ImageView imageView;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    /**
     * 菜品的适配器
     */
    public class DishesAdapter extends BaseAdapter {

        private List<Document> list;

        public DishesAdapter(List<Document> list) {

            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size() == 0 ? 0 : list.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater;

            layoutInflater = LayoutInflater.from(DishesActivity.this);
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.dishe_list_item, null);
                viewHolder.dishe_im = convertView.findViewById(R.id.dishe_im);
                viewHolder.disheName_tv = convertView.findViewById(R.id.dishe_tv);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();

            }
            viewHolder.disheName_tv.setText(list.get(position).getString("dishesName"));
            Blob taskBlob = list.get(position).getBlob("image");
            if (taskBlob != null) {
                Glide.with(DishesActivity.this).load(taskBlob.getContent()).into(viewHolder.dishe_im);

            } else {
                Glide.with(DishesActivity.this).load(R.mipmap.ic_launcher).into(viewHolder.dishe_im);
            }
            return convertView;
        }

        class ViewHolder {
            TextView disheName_tv;
            ImageView dishe_im;

        }
    }

    private void initkindList() {

        database = ((MyApplication) getApplicationContext()).getDatabase();

        //默认绑定dishesKindAdapter
        dishesKindAdapter = new DishesKindAdapter(dishesKindList);
        disheskind_Lv.setAdapter(dishesKindAdapter);

        //默认绑定dishes
        dishesAdapter = new DishesAdapter(dishesList);
        dishes_Lv.setAdapter(dishesAdapter);
        disheKindQuery();


        //kind的点击事件
        disheskind_Lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                dishesKindAdapter.changeSelected(position);
                kindPosition = position;
                //加载菜品的信息
                if (!dishesList.isEmpty()) {
                    dishesList.clear();
                }

                Document document = (Document) dishesKindList.get(position);

                Array array = document.getArray("dishesListId");


                if (array == null) {

                    return;
                }

                for (int i = 0; i < array.count(); i++) {

                    dishesList.add(database.getDocument(array.getString(i)));
                }

                dishesAdapter.notifyDataSetChanged();
            }
        });

        //默认选中第一项
        disheskind_Lv.performItemClick(disheskind_Lv.getChildAt(0), 0, disheskind_Lv
                .getItemIdAtPosition(0));
    }

    /**
     * 动态监听dishesKind更新信息
     */

    private void disheKindQuery() {
        //动态监听DisheKind信息
        query = Query.select(SelectResult.expression(Expression.meta().getId()))
                .from(DataSource.database(database))
                .where(Expression.property("className").equalTo("DishesKindC")).toLive();
        query.addChangeListener(new LiveQueryChangeListener() {
            @Override
            public void changed(LiveQueryChange change) {

                if (!dishesKindList.isEmpty()) {
                    dishesKindList.clear();
                }
                ResultSet rows = change.getRows();
                Result row = null;
                while ((row = rows.next()) != null) {

                    String id = row.getString(0);
                    Document doc = database.getDocument(id);
                    dishesKindList.add(doc);
                }

                dishesKindAdapter.notifyDataSetChanged();


            }
        });
        query.run();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void updataPosition(Integer integer) {

        //刷新菜品信息
        disheKindQuery();
        disheskind_Lv.performItemClick(disheskind_Lv.getChildAt(integer), integer, disheskind_Lv
                .getItemIdAtPosition(integer));
    }
}
