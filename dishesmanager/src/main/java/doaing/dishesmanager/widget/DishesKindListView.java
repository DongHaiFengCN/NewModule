
package doaing.dishesmanager.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;

import com.couchbase.lite.Meta;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.util.ArrayList;
import java.util.List;

import doaing.MyApplication;
import doaing.dishesmanager.adapter.DishesKindAdapter;




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
        dishesKindAdapter = new DishesKindAdapter(context,database);
        setAdapter(dishesKindAdapter);

    }

    public DishesKindListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void disheKindQuery() {


        //动态监听DisheKind信息
        Query query = QueryBuilder.select(SelectResult.expression(Meta.id))
                .from(DataSource.database(database))
                .where(Expression.property("className").equalTo(Expression.string("DishesKindC"))
                        .and(Expression.property("setMenu").equalTo(Expression.booleanValue(false))));
        ResultSet results = null;
        try {
            results = query.execute();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        Result row = null;

        while ((row = results.next()) != null) {

            String id = row.getString(0);
            Document doc = database.getDocument(id);
            dishesKindList.add(doc);
        }
    }
}

