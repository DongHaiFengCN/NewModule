package doaing.dishesmanager.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.LiveQueryChange;
import com.couchbase.lite.LiveQueryChangeListener;
import com.couchbase.lite.Query;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.util.ArrayList;
import java.util.List;

import doaing.dishesmanager.adapter.DishesKindAdapter;
import doaing.mylibrary.MyApplication;


/**
 * 项目名称：new
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2018/1/31 13:38
 * 修改人：donghaifeng
 * 修改时间：2018/1/31 13:38
 * 修改备注：
 */

public class DishesKindListView extends ListView {
    public DishesKindAdapter getDishesKindAdapter() {
        return dishesKindAdapter;
    }

    private DishesKindAdapter dishesKindAdapter;

    public List<Document> getDishesKindList() {
        return dishesKindList;
    }

    private List<Document> dishesKindList = new ArrayList<>();
    private Database database;
    public DishesKindListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        database =((MyApplication)context.getApplicationContext()).getDatabase();
        disheKindQuery();
        dishesKindAdapter = new DishesKindAdapter(dishesKindList, context);
        setAdapter(dishesKindAdapter);

    }

    public DishesKindListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void disheKindQuery() {


        //动态监听DisheKind信息
        LiveQuery query = Query.select(SelectResult.expression(Expression.meta().getId()))
                .from(DataSource.database(database))
                .where(Expression.property("className").equalTo("DishesKindC")
                        .and(Expression.property("setMenu").equalTo(false))).toLive();
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

            }
        });
        query.run();
    }
}
