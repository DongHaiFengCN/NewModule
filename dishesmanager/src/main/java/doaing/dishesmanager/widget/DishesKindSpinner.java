
package doaing.dishesmanager.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ArrayAdapter;

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

import tools.CDBHelper;


/**
 * 项目名称：new
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2018/1/22 14:20
 * 修改人：donghaifeng
 * 修改时间：2018/1/22 14:20
 * 修改备注：
 * @author donghaifeng
 */


public class DishesKindSpinner extends android.support.v7.widget.AppCompatSpinner {

    public List<Document> getDishesKindList() {
        return dishesKindList;
    }

    private List<Document> dishesKindList = new ArrayList<>();
    ArrayAdapter dishesKindAdapter;

    public DishesKindSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        dishesKindAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item);
        dishesKindAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setAdapter(dishesKindAdapter);
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

        final Database database = CDBHelper.getDatabase();
        Query query = QueryBuilder.select(SelectResult.expression(Meta.id))
                .from(DataSource.database(database))
                .where(Expression.property("className").equalTo(Expression.string("DishesKindC"))
                        .and(Expression.property("setMenu").equalTo(Expression. booleanValue(false))));

        try {
            if (!dishesKindList.isEmpty()){
                dishesKindList.clear();
            }
            if (dishesKindAdapter.getCount()>0){
                dishesKindAdapter.clear();
            }
            ResultSet results =query.execute();
            Result row ;
            while ((row = results.next()) != null) {
                String id = row.getString(0);
                Document doc = database.getDocument(id);
                dishesKindList.add(doc);
                dishesKindAdapter.add(doc.getString("kindName"));
            }
            dishesKindAdapter.notifyDataSetChanged();

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }


    }
}

