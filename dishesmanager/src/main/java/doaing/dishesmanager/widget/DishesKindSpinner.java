package doaing.dishesmanager.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;

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

import doaing.mylibrary.MyApplication;


/**
 * 项目名称：new
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2018/1/22 14:20
 * 修改人：donghaifeng
 * 修改时间：2018/1/22 14:20
 * 修改备注：
 */

public class DishesKindSpinner extends android.support.v7.widget.AppCompatSpinner {

    public List<Document> getDishesKindList() {
        return dishesKindList;
    }

    private List<Document> dishesKindList = new ArrayList<>();
    ArrayAdapter<String> Adapter;

    public DishesKindSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        Adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item);
        Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setAdapter(Adapter);
        getKindItems();
    }

    public DishesKindSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DishesKindSpinner(Context context) {
        super(context);


    }

    @Override
    public Object getSelectedItem() {
        return dishesKindList.get(getSelectedItemPosition());
    }
    private void getKindItems() {

        final Database database = ((MyApplication) getContext().getApplicationContext()).getDatabase();

        LiveQuery query = Query.select(SelectResult.expression(Expression.meta().getId()))
                .from(DataSource.database(database))
                .where(Expression.property("className").equalTo("DishesKindC")).toLive();
        query.addChangeListener(new LiveQueryChangeListener() {
            @Override
            public void changed(LiveQueryChange change) {

              if (!dishesKindList.isEmpty()){
                  dishesKindList.clear();
              }
              if (Adapter.getCount()>0){
                  Adapter.clear();
              }
                ResultSet rows = change.getRows();
                Result row = null;
                while ((row = rows.next()) != null) {


                    String id = row.getString(0);
                    Document doc = database.getDocument(id);
                    dishesKindList.add(doc);

                    Adapter.add(doc.getString("kindName").toString());
                }

            }
        });
        query.run();

    }
}
